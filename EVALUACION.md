# Evaluación de `ms_inventario`

**Fecha:** 2026-06-12
**Estado de compilación:** ✅ compila sin errores (`mvnw compile`)

**Resumen:** Es un microservicio Spring Boot 4 / Java 21 bien estructurado para su alcance (CRUD de productos y categorías + control de stock), con buenas prácticas de base: capas separadas, DTOs, validación, Flyway con `ddl-auto=validate`, transacciones y logging. Sin embargo, tiene **un bug de concurrencia en el endpoint más crítico (`descontar-stock`)**, manejo de errores que devuelve códigos HTTP incorrectos, y carece de tests reales. Nota general: **bueno como base académica/MVP, no listo para producción**.

## Fortalezas

- Arquitectura en capas limpia (Controller → Service → Repository) con DTOs separados de las entidades.
- Flyway con `spring.jpa.hibernate.ddl-auto=validate` — la combinación correcta.
- Validación con Bean Validation tanto en DTOs como en entidades, y constraints CHECK en la base de datos.
- Transacciones bien anotadas (`@Transactional(readOnly = true)` en lecturas).
- Paginación en los listados, excepciones de dominio propias y logging consistente.

## Hallazgos críticos

### 1. Condición de carrera en `descontarStock`
`InventarioService.java:137-156`

El patrón leer-verificar-escribir no está protegido: dos ventas concurrentes pueden leer el mismo `stockActual` y ambas pasar la validación, sobre-descontando stock. Es justamente el endpoint que llamará `ms-ventas`, así que la concurrencia es el caso esperado. El CHECK `stock_actual >= 0` en MySQL evita stock negativo, pero la segunda transacción fallaría con un error SQL crudo (que además se respondería como 400, ver hallazgo 3).

**Solución:** bloqueo pesimista (`@Lock(PESSIMISTIC_WRITE)` en un `findById` dedicado) o un UPDATE atómico tipo `UPDATE productos SET stock_actual = stock_actual - :c WHERE id = :id AND stock_actual >= :c` verificando filas afectadas.

### 2. `cantidad` sin validar en `descontar-stock`
`ControllerInventario.java:91`

No hay `@Min(1)` ni validación en el servicio. Una cantidad **negativa** pasa el chequeo de stock e *incrementa* el inventario a través del endpoint de descuento. Falta `@Validated` en el controller y `@Min(1)` en el parámetro.

### 3. El manejo global de excepciones devuelve 400 para todo
`GlobalExceptionHandler.java:60-67`

Cualquier error interno (NPE, caída de BD) responde `400 BAD_REQUEST` con el mensaje de la excepción expuesto al cliente. Debería ser 500 con mensaje genérico — el actual culpa al cliente de errores del servidor y filtra detalles internos.

Además, el handler de `SQLIntegrityConstraintViolationException` casi nunca se activará: Spring la envuelve en `DataIntegrityViolationException`, que cae al handler genérico. Efecto práctico: **borrar una categoría con productos asociados devuelve 400 con un mensaje SQL crudo**, en vez del 409 con mensaje claro que claramente se buscaba.

### 4. `codigo_barras` sin restricción UNIQUE
`V1__crear_tablas.sql`

Se pueden crear productos duplicados con el mismo código de barras, lo cual rompe el modelo de negocio de un kiosko (el escaneo resolvería ambiguo). Falta una migración `V2` que agregue el unique index. Relacionado: no existe endpoint de búsqueda por código de barras, que sería el flujo natural de una caja.

### 5. Credenciales hardcodeadas y configuración fija
`application.properties`

`root` sin contraseña y URL fija. Para un microservicio deberían externalizarse: `${DB_URL}`, `${DB_USER}`, `${DB_PASSWORD}`, idealmente con perfiles (`application-dev.properties` / `prod`). También `show-sql=true` no debería ir a producción.

## Hallazgos medios

- **`@Data` de Lombok en entidades JPA** (`Producto.java:14`, `Categoria.java:14`): genera `toString`/`equals`/`hashCode` sobre la relación bidireccional. `Producto.toString()` incluye `categoria` y `Categoria.toString()` incluye `productos` → riesgo de recursión infinita (StackOverflow) o `LazyInitializationException` al loguear/serializar. Práctica recomendada: `@Getter/@Setter` + excluir relaciones de `toString`/`equals`.
- **Excepción equivocada cuando la categoría no existe** (`InventarioService.java:60` y `:88`): se lanza `ProductoNoEncontradoException` con mensaje "Categoría no encontrada". Funciona (ambas dan 404) pero es inconsistente; existe `CategoriaNoEncontradaException` para esto.
- **N+1 queries en el listado paginado**: `mapToResponseDTO` accede a la categoría lazy de cada producto, generando una consulta por fila. Con `@EntityGraph(attributePaths = "categoria")` o un `join fetch` se resuelve.
- **Tests inexistentes en la práctica**: solo hay `contextLoads`, y como levanta el contexto completo contra MySQL local, falla si no hay BD corriendo. No hay tests unitarios del servicio (la lógica de stock es la candidata obvia) ni de integración con H2/Testcontainers.
- **Sin Actuator ni OpenAPI**: para un microservicio que será consumido por `ms-ventas`, faltan health checks (`spring-boot-starter-actuator`) y documentación de contrato (springdoc-openapi).

## Hallazgos menores

- `ExceptionDTO` usa `new Date().toString()` — formato no estándar y locale-dependiente; mejor `Instant.now()` (ISO-8601). También tiene imports de Lombok sin uso.
- Paquetes con mayúscula inicial (`Controller`, `Service`, `DTO`...) — la convención Java es minúsculas (`controller`, `service`, `dto`).
- `PUT /descontar-stock` no es idempotente (llamarlo dos veces descuenta dos veces); semánticamente corresponde `POST`.
- El `save()` en `actualizarProducto`/`actualizarCategoria` es redundante dentro de `@Transactional` (dirty checking lo hace solo) — inofensivo.
- La FK en la migración tiene nombre autogenerado por Hibernate (`FK2fwq10nwymfv7fumctxt9vpgb`); un nombre explícito (`fk_productos_categoria`) facilita el diagnóstico.

## Prioridad sugerida

1. Descuento de stock atómico + validación `@Min(1)` de cantidad (es el corazón del servicio).
2. Corregir el exception handler (500 para errores internos, capturar `DataIntegrityViolationException` como 409).
3. Migración V2 con UNIQUE en `codigo_barras` (+ endpoint de búsqueda por código).
4. Externalizar credenciales.
5. Tests del servicio de inventario.

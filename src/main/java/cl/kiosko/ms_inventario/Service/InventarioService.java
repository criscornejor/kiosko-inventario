package cl.kiosko.ms_inventario.Service;

import cl.kiosko.ms_inventario.DTO.CategoriaResponseDTO;
import cl.kiosko.ms_inventario.DTO.ProductoRequestDTO;
import cl.kiosko.ms_inventario.DTO.ProductoResponseDTO;
import cl.kiosko.ms_inventario.Exception.CategoriaNoEncontradaException;
import cl.kiosko.ms_inventario.Exception.ProductoNoEncontradoException;
import cl.kiosko.ms_inventario.Exception.StockInsuficienteException;
import cl.kiosko.ms_inventario.Model.Categoria;
import cl.kiosko.ms_inventario.Model.Producto;
import cl.kiosko.ms_inventario.Repository.CategoriaRepository;
import cl.kiosko.ms_inventario.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Obtiene una lista paginada de productos.
     * @param pageable Configuración de paginación
     * @return Página de ProductoResponseDTO
     */
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> obtenerProductos(Pageable pageable) {
        log.info("Obteniendo lista de productos (paginada)");
        return productoRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    /**
     * Obtiene un producto específico por su ID.
     * @param id Identificador del producto
     * @return ProductoResponseDTO
     */
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        log.info("Buscando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con id: " + id));
        return mapToResponseDTO(producto);
    }

    /**
     * Crea un nuevo producto validando la categoría existente.
     * @param requestDTO Datos del producto a crear
     * @return ProductoResponseDTO
     */
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {
        log.info("Iniciando creación de producto: {}", requestDTO.getNombre());
        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con id: " + requestDTO.getCategoriaId()));

        Producto producto = new Producto();
        producto.setNombre(requestDTO.getNombre());
        producto.setCodigoBarras(requestDTO.getCodigoBarras());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStockActual(requestDTO.getStockActual());
        producto.setStockMinimo(requestDTO.getStockMinimo());
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", guardado.getId());
        return mapToResponseDTO(guardado);
    }

    /**
     * Actualiza un producto existente.
     * @param id Identificador del producto
     * @param requestDTO Nuevos datos del producto
     * @return ProductoResponseDTO
     */
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO requestDTO) {
        log.info("Actualizando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con id: " + id));

        Categoria categoria = categoriaRepository.findById(requestDTO.getCategoriaId())
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoría no encontrada con id: " + requestDTO.getCategoriaId()));

        producto.setNombre(requestDTO.getNombre());
        producto.setCodigoBarras(requestDTO.getCodigoBarras());
        producto.setPrecio(requestDTO.getPrecio());
        producto.setStockActual(requestDTO.getStockActual());
        producto.setStockMinimo(requestDTO.getStockMinimo());
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);
        log.info("Producto ID {} actualizado exitosamente", id);
        return mapToResponseDTO(actualizado);
    }

    /**
     * Elimina lógicamente/físicamente un producto.
     * @param id Identificador del producto
     */
    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        if (!productoRepository.existsById(id)) {
            log.error("Fallo al eliminar: Producto ID {} no existe", id);
            throw new ProductoNoEncontradoException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
        log.info("Producto ID {} eliminado", id);
    }

    /**
     * Busca un producto por su código de barras (flujo de caja/escaneo).
     * @param codigoBarras Código de barras del producto
     * @return ProductoResponseDTO
     */
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorCodigoBarras(String codigoBarras) {
        log.info("Buscando producto con código de barras: {}", codigoBarras);
        Producto producto = productoRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con código de barras: " + codigoBarras));
        return mapToResponseDTO(producto);
    }

    /**
     * Verifica la cantidad de stock disponible para un producto.
     * @param id Identificador del producto
     * @return Cantidad de stock actual
     */
    @Transactional(readOnly = true)
    public Integer verificarStock(Long id) {
        log.info("Verificando stock para el producto ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con id: " + id));
        return producto.getStockActual();
    }

    /**
     * Descuenta una cantidad del stock actual si es posible.
     * Genera alerta (warning) si el stock queda por debajo del mínimo.
     * @param id Identificador del producto
     * @param cantidad Cantidad a descontar
     */
    @Transactional
    public void descontarStock(Long id, Integer cantidad) {
        log.info("Intentando descontar {} unidades del producto ID: {}", cantidad, id);
        Producto producto = productoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con id: " + id));

        if (producto.getStockActual() < cantidad) {
            log.warn("Intento de venta de producto ID {} con stock insuficiente", id);
            throw new StockInsuficienteException("Stock insuficiente para el producto ID: " + id);
        }

        producto.setStockActual(producto.getStockActual() - cantidad);
        productoRepository.save(producto);

        if (producto.getStockActual() < producto.getStockMinimo()) {
            log.warn("ALERTA DE STOCK: Producto ID {} ha caído por debajo del stock mínimo. Stock Actual: {}, Mínimo: {}", 
                    id, producto.getStockActual(), producto.getStockMinimo());
            // Aquí se podría publicar un evento para ms-notificaciones
        }
        log.info("Descuento exitoso. Nuevo stock para producto ID {}: {}", id, producto.getStockActual());
    }

    /**
     * Utilidad para mapear de Producto a ProductoResponseDTO
     */
    private ProductoResponseDTO mapToResponseDTO(Producto producto) {
        CategoriaResponseDTO catDTO = new CategoriaResponseDTO(
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getCategoria().getDescripcion()
        );

        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getCodigoBarras(),
                producto.getPrecio(),
                producto.getStockActual(),
                producto.getStockMinimo(),
                catDTO
        );
    }
}

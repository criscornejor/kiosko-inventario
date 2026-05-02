package cl.kiosko.ms_inventario.Controller;

import cl.kiosko.ms_inventario.DTO.ProductoRequestDTO;
import cl.kiosko.ms_inventario.DTO.ProductoResponseDTO;
import cl.kiosko.ms_inventario.Service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar el inventario de productos.
 */
@RestController
@RequestMapping("v1/kiosko/inventario/productos")
@RequiredArgsConstructor
public class ControllerInventario {

    private final InventarioService inventarioService;

    /**
     * Crea un nuevo producto.
     * @param requestDTO Datos del producto
     * @return Producto creado
     */
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO requestDTO) {
        return new ResponseEntity<>(inventarioService.crearProducto(requestDTO), HttpStatus.CREATED);
    }

    /**
     * Obtiene una lista paginada de productos.
     * @param pageable Paginación
     * @return Página de productos
     */
    @GetMapping
    public ResponseEntity<Page<ProductoResponseDTO>> obtenerProductos(Pageable pageable) {
        return ResponseEntity.ok(inventarioService.obtenerProductos(pageable));
    }

    /**
     * Obtiene un producto por su ID.
     * @param id ID del producto
     * @return Producto encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerProductoPorId(id));
    }

    /**
     * Actualiza un producto existente.
     * @param id ID del producto
     * @param requestDTO Nuevos datos
     * @return Producto actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO requestDTO) {
        return ResponseEntity.ok(inventarioService.actualizarProducto(id, requestDTO));
    }

    /**
     * Elimina un producto.
     * @param id ID del producto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        inventarioService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica el stock actual de un producto.
     * @param id ID del producto
     * @return Cantidad de stock
     */
    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> verificarStock(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.verificarStock(id));
    }

    /**
     * Descuenta stock de un producto (generalmente llamado por ms-ventas).
     * @param id ID del producto
     * @param cantidad Cantidad a descontar
     */
    @PutMapping("/{id}/descontar-stock")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        inventarioService.descontarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }
}

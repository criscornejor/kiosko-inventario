package cl.kiosko.ms_inventario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para devolver información de un Producto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String codigoBarras;
    private Integer precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private CategoriaResponseDTO categoria;
}

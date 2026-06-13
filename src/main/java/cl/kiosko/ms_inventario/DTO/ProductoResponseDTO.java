package cl.kiosko.ms_inventario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * DTO para devolver información de un Producto.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO extends RepresentationModel<ProductoResponseDTO> {
    private Long id;
    private String nombre;
    private String codigoBarras;
    private Integer precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private CategoriaResponseDTO categoria;
}

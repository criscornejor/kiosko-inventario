package cl.kiosko.ms_inventario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * DTO para devolver información de Categoría.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaResponseDTO extends RepresentationModel<CategoriaResponseDTO> {
    private Long id;
    private String nombre;
    private String descripcion;
}
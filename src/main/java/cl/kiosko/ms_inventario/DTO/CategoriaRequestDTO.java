package cl.kiosko.ms_inventario.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoria es obligatorio")
    private String nombre;

    private String descripcion;
}

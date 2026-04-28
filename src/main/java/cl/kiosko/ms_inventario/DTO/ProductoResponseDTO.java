package cl.kiosko.ms_inventario.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private Double precio;
}

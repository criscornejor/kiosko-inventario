package cl.kiosko.ms_inventario.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100,  nullable = false)
    @NotBlank(message = "El nombre se debe especificar, no puede no ingresar un nombre")
    private String nombre;
    @Column(length = 100,  nullable = false)
    @NotBlank(message = "Debe ingresar una descripción")
    private String descripcion;
}

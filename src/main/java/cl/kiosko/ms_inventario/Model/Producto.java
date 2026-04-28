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
@Table(name = "Producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(length = 50,  nullable = false)
    @NotBlank
    private String nombre;
    @Column(length = 100,  nullable = false)
    @NotBlank
    private String codigoBarras;
    @Column
    @NotNull
    private int precio;
    @Column
    @NotNull
    private int stockActual;
}

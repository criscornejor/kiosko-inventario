package cl.kiosko.ms_inventario.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa una Categoría de productos en el kiosko.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Debe ingresar un nombre, no puede dejarlo en blanco")
    private String nombre;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Debe ingresar una descripción")
    private String descripcion;

    /**
     * Relación uno a muchos con Producto. 
     * Se usa CascadeType.RESTRICT (o por defecto sin cascada de borrado) para evitar 
     * eliminar categorías que tengan productos asociados (prevención de borrado).
     */
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;
}

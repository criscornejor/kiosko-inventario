package cl.kiosko.ms_inventario.Repository;

import cl.kiosko.ms_inventario.Model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Producto.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    /**
     * Busca un producto bloqueando la fila (SELECT ... FOR UPDATE) para
     * operaciones de stock que deben ser atómicas frente a concurrencia.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);
}

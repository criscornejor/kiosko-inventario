package cl.kiosko.ms_inventario.Service;

import cl.kiosko.ms_inventario.Repository.CategoriaRepository;
import cl.kiosko.ms_inventario.Repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventarioService {
    @Autowired
    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;


}

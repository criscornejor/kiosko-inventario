package cl.kiosko.ms_inventario.Service;

import cl.kiosko.ms_inventario.DTO.CategoriaRequestDTO;
import cl.kiosko.ms_inventario.DTO.CategoriaResponseDTO;
import cl.kiosko.ms_inventario.Exception.CategoriaNoEncontradaException;
import cl.kiosko.ms_inventario.Exception.StockInsuficienteException;
import cl.kiosko.ms_inventario.Model.Categoria;
import cl.kiosko.ms_inventario.Model.Producto;
import cl.kiosko.ms_inventario.Repository.CategoriaRepository;
import cl.kiosko.ms_inventario.Repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    CategoriaRepository categoriaRepository;

    @Mock
    ProductoRepository productoRepository;

    @InjectMocks
    CategoriaService categoriaService;

    private Producto ProductoMock;
    private Categoria CategoriaMock;

    @BeforeEach
    void setUp() {
        CategoriaMock = new Categoria();
        CategoriaMock.setId(1L);
        CategoriaMock.setNombre("Bebidas");
        CategoriaMock.setDescripcion("Bebidas frias");

        ProductoMock = new Producto();
        ProductoMock.setId(10L);
        ProductoMock.setNombre("Pepsi");
        ProductoMock.setCodigoBarras("12345");
        ProductoMock.setPrecio(1500);
        ProductoMock.setStockActual(20);
        ProductoMock.setStockMinimo(5);
        ProductoMock.setCategoria(CategoriaMock);
    }

    @Test
    void getCategoriaById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(CategoriaMock));

        CategoriaResponseDTO response = categoriaService.obtenerCategoriaPorId(1L);

        assertNotNull(response);
        assertEquals("Bebidas", response.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
    }
}

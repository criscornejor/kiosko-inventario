package cl.kiosko.ms_inventario.Service;

import cl.kiosko.ms_inventario.Client.NotificacionesClient;
import cl.kiosko.ms_inventario.DTO.ProductoRequestDTO;
import cl.kiosko.ms_inventario.DTO.ProductoResponseDTO;
import cl.kiosko.ms_inventario.Exception.ProductoNoEncontradoException;
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
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private NotificacionesClient notificacionesClient;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto productoMock;
    private Categoria categoriaMock;

    @BeforeEach
    void setUp() {
        categoriaMock = new Categoria();
        categoriaMock.setId(1L);
        categoriaMock.setNombre("Bebidas");
        categoriaMock.setDescripcion("Bebidas frias");

        productoMock = new Producto();
        productoMock.setId(10L);
        productoMock.setNombre("Coca Cola");
        productoMock.setCodigoBarras("12345");
        productoMock.setPrecio(1500);
        productoMock.setStockActual(20);
        productoMock.setStockMinimo(5);
        productoMock.setCategoria(categoriaMock);
    }

    @Test
    void obtenerProductoPorId_Exito() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMock));

        ProductoResponseDTO response = inventarioService.obtenerProductoPorId(10L);

        assertNotNull(response);
        assertEquals("Coca Cola", response.getNombre());
        assertEquals("Bebidas", response.getCategoria().getNombre());
        verify(productoRepository, times(1)).findById(10L);
    }

    @Test
    void obtenerProductoPorId_NoEncontrado() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class, () -> inventarioService.obtenerProductoPorId(99L));
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    void crearProducto_Exito() {
        ProductoRequestDTO requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Sprite");
        requestDTO.setCodigoBarras("54321");
        requestDTO.setPrecio(1200);
        requestDTO.setStockActual(10);
        requestDTO.setStockMinimo(2);
        requestDTO.setCategoriaId(1L);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMock));

        Producto productoGuardado = new Producto();
        productoGuardado.setId(11L);
        productoGuardado.setNombre("Sprite");
        productoGuardado.setCodigoBarras("54321");
        productoGuardado.setPrecio(1200);
        productoGuardado.setStockActual(10);
        productoGuardado.setStockMinimo(2);
        productoGuardado.setCategoria(categoriaMock);

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        ProductoResponseDTO response = inventarioService.crearProducto(requestDTO);

        assertNotNull(response);
        assertEquals(11L, response.getId());
        assertEquals("Sprite", response.getNombre());
        verify(categoriaRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void descontarStock_Exito() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        inventarioService.descontarStock(10L, 5);

        assertEquals(15, productoMock.getStockActual());
        verify(productoRepository, times(1)).save(productoMock);
    }

    @Test
    void descontarStock_StockInsuficiente() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMock));

        assertThrows(StockInsuficienteException.class, () -> inventarioService.descontarStock(10L, 25));
        verify(productoRepository, never()).save(any(Producto.class));
    }
}

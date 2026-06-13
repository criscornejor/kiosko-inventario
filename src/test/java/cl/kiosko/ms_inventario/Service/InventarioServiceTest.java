package cl.kiosko.ms_inventario.Service;

import cl.kiosko.ms_inventario.DTO.ProductoRequestDTO;
import cl.kiosko.ms_inventario.DTO.ProductoResponseDTO;
import cl.kiosko.ms_inventario.Exception.CategoriaNoEncontradaException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Bebidas", "Bebidas y jugos", null);
        producto = new Producto(1L, "Coca-Cola 500ml", "7801234567890", 1500, 10, 3, categoria);
    }

    @Test
    void descontarStock_conStockSuficiente_descuentaCorrectamente() {
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        inventarioService.descontarStock(1L, 4);

        assertThat(producto.getStockActual()).isEqualTo(6);
        verify(productoRepository).save(producto);
    }

    @Test
    void descontarStock_conStockInsuficiente_lanzaExcepcionYNoModificaStock() {
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> inventarioService.descontarStock(1L, 11))
                .isInstanceOf(StockInsuficienteException.class);

        assertThat(producto.getStockActual()).isEqualTo(10);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void descontarStock_productoInexistente_lanzaNotFound() {
        when(productoRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.descontarStock(99L, 1))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }

    @Test
    void descontarStock_dejandoStockExacto_permiteVaciarStock() {
        when(productoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(producto));

        inventarioService.descontarStock(1L, 10);

        assertThat(producto.getStockActual()).isZero();
    }

    @Test
    void verificarStock_productoExistente_retornaStockActual() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThat(inventarioService.verificarStock(1L)).isEqualTo(10);
    }

    @Test
    void crearProducto_conCategoriaInexistente_lanzaCategoriaNoEncontrada() {
        ProductoRequestDTO request = new ProductoRequestDTO("Coca-Cola 500ml", "7801234567890", 1500, 10, 3, 99L);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.crearProducto(request))
                .isInstanceOf(CategoriaNoEncontradaException.class);

        verify(productoRepository, never()).save(any());
    }

    @Test
    void crearProducto_valido_retornaDTOConDatosGuardados() {
        ProductoRequestDTO request = new ProductoRequestDTO("Coca-Cola 500ml", "7801234567890", 1500, 10, 3, 1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDTO response = inventarioService.crearProducto(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Coca-Cola 500ml");
        assertThat(response.getCategoria().getId()).isEqualTo(1L);
    }

    @Test
    void obtenerProductoPorCodigoBarras_existente_retornaProducto() {
        when(productoRepository.findByCodigoBarras("7801234567890")).thenReturn(Optional.of(producto));

        ProductoResponseDTO response = inventarioService.obtenerProductoPorCodigoBarras("7801234567890");

        assertThat(response.getCodigoBarras()).isEqualTo("7801234567890");
    }

    @Test
    void obtenerProductoPorCodigoBarras_inexistente_lanzaNotFound() {
        when(productoRepository.findByCodigoBarras("000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.obtenerProductoPorCodigoBarras("000"))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }

    @Test
    void obtenerProductoPorId_inexistente_lanzaNotFound() {
        when(productoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventarioService.obtenerProductoPorId(5L))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }
}

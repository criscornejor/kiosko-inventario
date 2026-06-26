package cl.kiosko.ms_inventario.Controller;

import cl.kiosko.ms_inventario.DTO.CategoriaResponseDTO;
import cl.kiosko.ms_inventario.DTO.ProductoRequestDTO;
import cl.kiosko.ms_inventario.DTO.ProductoResponseDTO;
import cl.kiosko.ms_inventario.Service.InventarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerInventarioTest {

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private ControllerInventario controller;

    @Test
    void crearProducto_RetornaCreatedConLinks() {
        ProductoRequestDTO request = productoRequest();
        when(inventarioService.crearProducto(request)).thenReturn(productoResponse());

        ResponseEntity<ProductoResponseDTO> response = controller.crearProducto(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().hasLink("self"));
    }

    @Test
    void obtenerProductos_RetornaPageConLinks() {
        Page<ProductoResponseDTO> page = new PageImpl<>(List.of(productoResponse()));
        when(inventarioService.obtenerProductos(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ProductoResponseDTO>> response = controller.obtenerProductos(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).hasLink("self"));
    }

    @Test
    void descontarStock_PropagaAuthorizationYRetornaOk() {
        ResponseEntity<Void> response = controller.descontarStock(1L, 2, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(inventarioService).descontarStock(1L, 2, "Bearer token");
    }

    private ProductoRequestDTO productoRequest() {
        return new ProductoRequestDTO("Coca Cola", "123", 1500, 10, 2, 1L);
    }

    private ProductoResponseDTO productoResponse() {
        return new ProductoResponseDTO(1L, "Coca Cola", "123", 1500, 10, 2,
                new CategoriaResponseDTO(1L, "Bebidas", "Bebidas frias"));
    }
}

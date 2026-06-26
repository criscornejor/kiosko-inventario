package cl.kiosko.ms_inventario.Controller;

import cl.kiosko.ms_inventario.DTO.CategoriaRequestDTO;
import cl.kiosko.ms_inventario.DTO.CategoriaResponseDTO;
import cl.kiosko.ms_inventario.Service.CategoriaService;
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
class ControllerCategoriaTest {

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private ControllerCategoria controller;

    @Test
    void crearCategoria_RetornaCreatedConLinks() {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Bebidas", "Bebidas frias");
        when(categoriaService.crearCategoria(request)).thenReturn(new CategoriaResponseDTO(1L, "Bebidas", "Bebidas frias"));

        ResponseEntity<CategoriaResponseDTO> response = controller.crearCategoria(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().hasLink("self"));
        verify(categoriaService).crearCategoria(request);
    }

    @Test
    void obtenerCategorias_RetornaPageConLinks() {
        Page<CategoriaResponseDTO> page = new PageImpl<>(List.of(new CategoriaResponseDTO(1L, "Bebidas", "Bebidas frias")));
        when(categoriaService.obtenerCategorias(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<CategoriaResponseDTO>> response = controller.obtenerCategorias(Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        assertTrue(response.getBody().getContent().get(0).hasLink("self"));
    }

    @Test
    void eliminarCategoria_RetornaNoContent() {
        ResponseEntity<Void> response = controller.eliminarCategoria(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoriaService).eliminarCategoria(1L);
    }
}

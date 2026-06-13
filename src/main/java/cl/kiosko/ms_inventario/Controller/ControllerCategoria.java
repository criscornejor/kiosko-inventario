package cl.kiosko.ms_inventario.Controller;

import cl.kiosko.ms_inventario.DTO.CategoriaRequestDTO;
import cl.kiosko.ms_inventario.DTO.CategoriaResponseDTO;
import cl.kiosko.ms_inventario.Service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("v1/kiosko/inventario/categorias")
@RequiredArgsConstructor
public class ControllerCategoria {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaRequestDTO requestDTO) {
        CategoriaResponseDTO response = categoriaService.crearCategoria(requestDTO);
        response.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategoriaPorId(response.getId())).withSelfRel());
        response.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategorias(null)).withRel("categorias"));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> obtenerCategorias(Pageable pageable) {
        Page<CategoriaResponseDTO> categorias = categoriaService.obtenerCategorias(pageable);
        categorias.forEach(categoria -> 
            categoria.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategoriaPorId(categoria.getId())).withSelfRel())
        );
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaResponseDTO response = categoriaService.obtenerCategoriaPorId(id);
        response.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategoriaPorId(id)).withSelfRel());
        response.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategorias(null)).withRel("categorias"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO requestDTO) {
        CategoriaResponseDTO response = categoriaService.actualizarCategoria(id, requestDTO);
        response.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategoriaPorId(id)).withSelfRel());
        response.add(linkTo(methodOn(ControllerCategoria.class).obtenerCategorias(null)).withRel("categorias"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}

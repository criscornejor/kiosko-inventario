package cl.kiosko.ms_inventario.Service;

import cl.kiosko.ms_inventario.DTO.CategoriaRequestDTO;
import cl.kiosko.ms_inventario.DTO.CategoriaResponseDTO;
import cl.kiosko.ms_inventario.Exception.CategoriaNoEncontradaException;
import cl.kiosko.ms_inventario.Model.Categoria;
import cl.kiosko.ms_inventario.Repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public Page<CategoriaResponseDTO> obtenerCategorias(Pageable pageable) {
        log.info("Obteniendo lista de categorias (paginada)");
        return categoriaRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        log.info("Buscando categoria con ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con id: " + id));
        return mapToResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO requestDTO) {
        log.info("Iniciando creación de categoria: {}", requestDTO.getNombre());
        Categoria categoria = new Categoria();
        categoria.setNombre(requestDTO.getNombre());
        categoria.setDescripcion(requestDTO.getDescripcion());

        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoria creada exitosamente con ID: {}", guardada.getId());
        return mapToResponseDTO(guardada);
    }

    @Transactional
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO requestDTO) {
        log.info("Actualizando categoria con ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con id: " + id));

        categoria.setNombre(requestDTO.getNombre());
        categoria.setDescripcion(requestDTO.getDescripcion());

        Categoria actualizada = categoriaRepository.save(categoria);
        log.info("Categoria ID {} actualizada exitosamente", id);
        return mapToResponseDTO(actualizada);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoria con ID: {}", id);
        if (!categoriaRepository.existsById(id)) {
            log.error("Fallo al eliminar: Categoria ID {} no existe", id);
            throw new CategoriaNoEncontradaException("Categoria no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
        log.info("Categoria ID {} eliminada", id);
    }

    private CategoriaResponseDTO mapToResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}

package etctech.gestionventas.service.impl;

import etctech.gestionventas.dto.request.CategoriaRequestDTO;
import etctech.gestionventas.dto.response.CategoriaResponseDTO;
import etctech.gestionventas.entity.Categoria;
import etctech.gestionventas.exception.BusinessException;
import etctech.gestionventas.exception.ResourceNotFoundException;
import etctech.gestionventas.repository.CategoriaRepository;
import etctech.gestionventas.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request) {
        log.info("Creando nueva categoría: {}", request.getNombre());

        // Validar que no exista una categoría con el mismo nombre
        if (categoriaRepository.existsByNombreAndEstadoTrue(request.getNombre())) {
            throw new BusinessException("Ya existe una categoría activa con el nombre: " + request.getNombre());
        }

        // Crear la entidad
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre().trim());
        categoria.setEstado(true);

        // Guardar
        Categoria saved = categoriaRepository.save(categoria);
        log.info("Categoría creada exitosamente con ID: {}", saved.getIdCategoria());

        return mapToResponseDTO(saved);
    }

    @Override
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO request) {
        log.info("Actualizando categoría con ID: {}", id);

        // Buscar la categoría activa
        Categoria categoria = categoriaRepository.findByIdCategoriaAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Validar que el nombre no exista en otra categoría activa
        if (categoriaRepository.existsByNombreAndEstadoTrueAndIdCategoriaNot(request.getNombre().trim(), id)) {
            throw new BusinessException("Ya existe otra categoría activa con el nombre: " + request.getNombre());
        }

        // Actualizar
        categoria.setNombre(request.getNombre().trim());
        Categoria updated = categoriaRepository.save(categoria);
        log.info("Categoría actualizada exitosamente con ID: {}", updated.getIdCategoria());

        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        log.info("Obteniendo categoría con ID: {}", id);

        Categoria categoria = categoriaRepository.findByIdCategoriaAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        return mapToResponseDTO(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResponseDTO> listarCategoriasActivas(Pageable pageable) {
        log.info("Listando categorías activas con paginación");

        return categoriaRepository.findByEstadoTrue(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategoriasActivas() {
        log.info("Listando todas las categorías activas");

        return categoriaRepository.findByEstadoTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResponseDTO> buscarCategoriasPorNombre(String nombre, Pageable pageable) {
        log.info("Buscando categorías por nombre: {}", nombre);

        return categoriaRepository.findByNombreContainingIgnoreCaseAndEstadoTrue(nombre, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public void eliminarCategoria(Long id) {
        log.info("Eliminando lógicamente categoría con ID: {}", id);

        Categoria categoria = categoriaRepository.findByIdCategoriaAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Verificar si la categoría tiene productos asociados activos
        boolean tieneProductosActivos = categoria.getProductos().stream()
                .anyMatch(producto -> producto.getEstado() != null && producto.getEstado());

        if (tieneProductosActivos) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene productos activos asociados");
        }

        // Eliminación lógica
        categoria.setEstado(false);
        categoriaRepository.save(categoria);
        log.info("Categoría eliminada lógicamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCategoriaActiva(Long id) {
        return categoriaRepository.findByIdCategoriaAndEstadoTrue(id).isPresent();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private CategoriaResponseDTO mapToResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .estado(categoria.getEstado())
                .build();
    }

}
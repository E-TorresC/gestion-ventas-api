package etctech.gestionventas.service;

import etctech.gestionventas.dto.request.CategoriaRequestDTO;
import etctech.gestionventas.dto.response.CategoriaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO request);

    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO request);

    CategoriaResponseDTO obtenerCategoriaPorId(Long id);

    Page<CategoriaResponseDTO> listarCategoriasActivas(Pageable pageable);

    List<CategoriaResponseDTO> listarCategoriasActivas();

    Page<CategoriaResponseDTO> buscarCategoriasPorNombre(String nombre, Pageable pageable);

    void eliminarCategoria(Long id);

    boolean existeCategoriaActiva(Long id);

}
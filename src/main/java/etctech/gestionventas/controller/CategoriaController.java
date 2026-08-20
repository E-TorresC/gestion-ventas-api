package etctech.gestionventas.controller;

import etctech.gestionventas.dto.request.CategoriaRequestDTO;
import etctech.gestionventas.dto.response.ApiResponseDTO;
import etctech.gestionventas.dto.response.CategoriaResponseDTO;
import etctech.gestionventas.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * Crear una nueva categoría
     * POST /api/categorias
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<CategoriaResponseDTO>> crearCategoria(
            @Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO response = categoriaService.crearCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.created(response));
    }

    /**
     * Actualizar una categoría existente
     * PUT /api/categorias/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoriaResponseDTO>> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO response = categoriaService.actualizarCategoria(id, request);
        return ResponseEntity.ok(ApiResponseDTO.success(response, "Categoría actualizada exitosamente"));
    }

    /**
     * Obtener una categoría por ID
     * GET /api/categorias/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CategoriaResponseDTO>> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaResponseDTO response = categoriaService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(ApiResponseDTO.success(response));
    }

    /**
     * Listar todas las categorías activas con paginación
     * GET /api/categorias
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<CategoriaResponseDTO>>> listarCategorias(
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CategoriaResponseDTO> page = categoriaService.listarCategoriasActivas(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Listar todas las categorías activas (sin paginación)
     * GET /api/categorias/todos
     */
    @GetMapping("/todos")
    public ResponseEntity<ApiResponseDTO<List<CategoriaResponseDTO>>> listarTodasCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarCategoriasActivas();
        return ResponseEntity.ok(ApiResponseDTO.success(categorias));
    }

    /**
     * Buscar categorías por nombre
     * GET /api/categorias/buscar?nombre=...
     */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<Page<CategoriaResponseDTO>>> buscarCategoriasPorNombre(
            @RequestParam String nombre,
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CategoriaResponseDTO> page = categoriaService.buscarCategoriasPorNombre(nombre, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Eliminar lógicamente una categoría
     * DELETE /api/categorias/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponseDTO.deleted());
    }

    /**
     * Verificar si existe una categoría activa
     * GET /api/categorias/{id}/existe
     */
    @GetMapping("/{id}/existe")
    public ResponseEntity<ApiResponseDTO<Boolean>> existeCategoria(@PathVariable Long id) {
        boolean existe = categoriaService.existeCategoriaActiva(id);
        return ResponseEntity.ok(ApiResponseDTO.success(existe));
    }

}
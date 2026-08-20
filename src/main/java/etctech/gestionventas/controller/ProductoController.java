package etctech.gestionventas.controller;

import etctech.gestionventas.dto.request.ProductoFiltroDTO;
import etctech.gestionventas.dto.request.ProductoRequestDTO;
import etctech.gestionventas.dto.response.ApiResponseDTO;
import etctech.gestionventas.dto.response.ProductoResponseDTO;
import etctech.gestionventas.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Crear un nuevo producto
     * POST /api/productos
     */
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO response = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar un producto existente
     * PUT /api/productos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO response = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener un producto por ID
     * GET /api/productos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        ProductoResponseDTO response = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Listar productos activos con paginación
     * GET /api/productos
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<ProductoResponseDTO>>> listarProductos(
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "nombre", direction = Sort.Direction.ASC)
            }) Pageable pageable) {

        Page<ProductoResponseDTO> page = productoService.listarProductosActivos(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Buscar productos con filtros dinámicos (Specifications)
     * POST /api/productos/buscar
     */
    @PostMapping("/buscar")
    public ResponseEntity<ApiResponseDTO<Page<ProductoResponseDTO>>> buscarProductosConFiltros(
            @RequestBody(required = false) ProductoFiltroDTO filtro,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "nombre", direction = Sort.Direction.ASC)
            }) Pageable pageable) {

        if (filtro == null) {
            filtro = new ProductoFiltroDTO();
        }

        Page<ProductoResponseDTO> page = productoService.buscarProductosConFiltros(filtro, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(page));
    }

    /**
     * Buscar productos por nombre
     * GET /api/productos/buscar/nombre?nombre=...
     */
    @GetMapping("/buscar/nombre")
    public ResponseEntity<Page<ProductoResponseDTO>> buscarProductosPorNombre(
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductoResponseDTO> page = productoService.buscarProductosPorNombre(nombre, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Buscar productos por categoría
     * GET /api/productos/categoria/{idCategoria}
     */
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<Page<ProductoResponseDTO>> buscarProductosPorCategoria(
            @PathVariable Long idCategoria,
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ProductoResponseDTO> page = productoService.buscarProductosPorCategoria(idCategoria, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Eliminar lógicamente un producto
     * DELETE /api/productos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verificar si existe un producto activo
     * GET /api/productos/{id}/existe
     */
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeProducto(@PathVariable Long id) {
        boolean existe = productoService.existeProductoActivo(id);
        return ResponseEntity.ok(existe);
    }

    /**
     * Obtener productos con stock bajo
     * GET /api/productos/stock-bajo?limite=10
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<ApiResponseDTO<List<ProductoResponseDTO>>> obtenerProductosStockBajo(
            @RequestParam(defaultValue = "5") Integer limite) {
        List<ProductoResponseDTO> productos = productoService.obtenerProductosStockBajo(limite);
        return ResponseEntity.ok(ApiResponseDTO.success(productos));
    }



}
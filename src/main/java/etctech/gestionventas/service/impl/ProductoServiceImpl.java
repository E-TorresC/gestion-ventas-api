package etctech.gestionventas.service.impl;

import etctech.gestionventas.dto.request.ProductoFiltroDTO;
import etctech.gestionventas.dto.request.ProductoRequestDTO;
import etctech.gestionventas.dto.response.CategoriaResponseDTO;
import etctech.gestionventas.dto.response.ProductoResponseDTO;
import etctech.gestionventas.entity.Categoria;
import etctech.gestionventas.entity.Producto;
import etctech.gestionventas.exception.BusinessException;
import etctech.gestionventas.exception.ResourceNotFoundException;
import etctech.gestionventas.repository.CategoriaRepository;
import etctech.gestionventas.repository.ProductoRepository;
import etctech.gestionventas.service.ProductoService;
import etctech.gestionventas.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        log.info("Creando nuevo producto: {}", request.getNombre());

        // Validar que no exista un producto con el mismo nombre
        if (productoRepository.existsByNombreAndEstadoTrue(request.getNombre().trim())) {
            throw new BusinessException("Ya existe un producto activo con el nombre: " + request.getNombre());
        }

        // Validar que la categoría exista y esté activa
        Categoria categoria = categoriaRepository.findByIdCategoriaAndEstadoTrue(request.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", request.getIdCategoria()));

        // Crear el producto
        Producto producto = new Producto();
        producto.setNombre(request.getNombre().trim());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);
        producto.setEstado(true);

        Producto saved = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", saved.getIdProducto());

        return mapToResponseDTO(saved);
    }

    @Override
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request) {
        log.info("Actualizando producto con ID: {}", id);

        // Buscar el producto activo
        Producto producto = productoRepository.findByIdProductoAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        // Validar que el nombre no exista en otro producto activo
        if (productoRepository.existsByNombreAndEstadoTrueAndIdProductoNot(request.getNombre().trim(), id)) {
            throw new BusinessException("Ya existe otro producto activo con el nombre: " + request.getNombre());
        }

        // Validar que la categoría exista y esté activa
        Categoria categoria = categoriaRepository.findByIdCategoriaAndEstadoTrue(request.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", request.getIdCategoria()));

        // Actualizar datos
        producto.setNombre(request.getNombre().trim());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoria(categoria);

        Producto updated = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente con ID: {}", updated.getIdProducto());

        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        log.info("Obteniendo producto con ID: {}", id);

        Producto producto = productoRepository.findByIdProductoAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        return mapToResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarProductosActivos(Pageable pageable) {
        log.info("Listando productos activos con paginación");

        return productoRepository.findByEstadoTrue(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> buscarProductosConFiltros(ProductoFiltroDTO filtro, Pageable pageable) {
        log.info("Buscando productos con filtros: {}", filtro);

        Specification<Producto> spec = ProductoSpecification.filtrarProductos(filtro);
        return productoRepository.findAll(spec, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> buscarProductosPorNombre(String nombre, Pageable pageable) {
        log.info("Buscando productos por nombre: {}", nombre);

        if (nombre == null || nombre.trim().isEmpty()) {
            return listarProductosActivos(pageable);
        }

        return productoRepository.findByNombreContainingIgnoreCaseAndEstadoTrue(nombre.trim(), pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> buscarProductosPorCategoria(Long idCategoria, Pageable pageable) {
        log.info("Buscando productos por categoría ID: {}", idCategoria);

        // Verificar que la categoría exista
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new ResourceNotFoundException("Categoría", "id", idCategoria);
        }

        return productoRepository.findByCategoriaIdCategoriaAndEstadoTrue(idCategoria, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public void eliminarProducto(Long id) {
        log.info("Eliminando lógicamente producto con ID: {}", id);

        Producto producto = productoRepository.findByIdProductoAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        // Verificar si el producto tiene detalles de pedido activos
        boolean tieneDetallesActivos = producto.getDetallesPedido().stream()
                .anyMatch(detalle -> detalle.getPedido() != null &&
                        detalle.getPedido().getEstado() != null &&
                        !detalle.getPedido().getEstado().equals("ANULADO") &&
                        !detalle.getPedido().getEstado().equals("ENTREGADO"));

        if (tieneDetallesActivos) {
            throw new BusinessException("No se puede eliminar el producto porque tiene pedidos activos asociados");
        }

        // Eliminación lógica
        producto.setEstado(false);
        productoRepository.save(producto);
        log.info("Producto eliminado lógicamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeProductoActivo(Long id) {
        return productoRepository.findByIdProductoAndEstadoTrue(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerProductosStockBajo(Integer limite) {
        log.info("Obteniendo productos con stock menor a: {}", limite);

        return productoRepository.findByStockLessThanAndEstadoTrue(limite)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTop5ProductosMasVendidos() {
        log.info("Obteniendo top 5 productos más vendidos");

        Pageable top5 = Pageable.ofSize(5);
        return productoRepository.findTop5ProductosMasVendidos(top5)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private ProductoResponseDTO mapToResponseDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .estado(producto.getEstado())
                .categoria(mapToCategoriaResponseDTO(producto.getCategoria()))
                .build();
    }

    private CategoriaResponseDTO mapToCategoriaResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .estado(categoria.getEstado())
                .build();
    }

}
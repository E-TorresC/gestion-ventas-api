package etctech.gestionventas.service;

import etctech.gestionventas.dto.request.ProductoFiltroDTO;
import etctech.gestionventas.dto.request.ProductoRequestDTO;
import etctech.gestionventas.dto.response.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO request);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    ProductoResponseDTO obtenerProductoPorId(Long id);

    Page<ProductoResponseDTO> listarProductosActivos(Pageable pageable);

    Page<ProductoResponseDTO> buscarProductosConFiltros(ProductoFiltroDTO filtro, Pageable pageable);

    Page<ProductoResponseDTO> buscarProductosPorNombre(String nombre, Pageable pageable);

    Page<ProductoResponseDTO> buscarProductosPorCategoria(Long idCategoria, Pageable pageable);

    void eliminarProducto(Long id);

    boolean existeProductoActivo(Long id);

    List<ProductoResponseDTO> obtenerProductosStockBajo(Integer limite);

    List<ProductoResponseDTO> obtenerTop5ProductosMasVendidos();

}
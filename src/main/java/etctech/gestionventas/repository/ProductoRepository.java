package etctech.gestionventas.repository;

import etctech.gestionventas.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // Listar solo productos activos con paginación
    Page<Producto> findByEstadoTrue(Pageable pageable);

    // Listar solo productos activos sin paginación
    List<Producto> findByEstadoTrue();

    // Buscar producto activo por ID
    Optional<Producto> findByIdProductoAndEstadoTrue(Long id);

    // Verificar si existe un producto activo con ese nombre
    boolean existsByNombreAndEstadoTrue(String nombre);

    // Verificar si existe un producto activo con ese nombre (excluyendo un ID)
    @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE p.nombre = :nombre AND p.estado = true AND p.idProducto != :id")
    boolean existsByNombreAndEstadoTrueAndIdProductoNot(@Param("nombre") String nombre, @Param("id") Long id);

    // Buscar productos por categoría
    Page<Producto> findByCategoriaIdCategoriaAndEstadoTrue(Long idCategoria, Pageable pageable);

    // Buscar productos por nombre (contiene)
    Page<Producto> findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre, Pageable pageable);

    // Contar productos activos
    long countByEstadoTrue();

    // Obtener productos con stock bajo (para reporte)
    List<Producto> findByStockLessThanAndEstadoTrue(Integer stockMinimo);

    // Top 5 productos más vendidos (para reporte)
    @Query("SELECT p FROM Producto p JOIN p.detallesPedido dp " +
            "WHERE p.estado = true AND dp.pedido.estado != 'ANULADO' " +
            "GROUP BY p.idProducto, p.nombre, p.precio, p.stock, p.estado, p.categoria " +
            "ORDER BY SUM(dp.cantidad) DESC")
    List<Producto> findTop5ProductosMasVendidos(Pageable pageable);

    // Buscar con Specifications y paginación
    Page<Producto> findAll(Specification<Producto> spec, Pageable pageable);

    // Contar con Specifications
    long count(Specification<Producto> spec);

    // Listar con Specifications sin paginación
    List<Producto> findAll(Specification<Producto> spec);

}
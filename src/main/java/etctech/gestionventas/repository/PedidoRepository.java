package etctech.gestionventas.repository;

import etctech.gestionventas.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>, JpaSpecificationExecutor<Pedido> {

    // Buscar pedido por ID (incluyendo anulados)
    Optional<Pedido> findById(Long id);

    // Buscar pedidos de un cliente
    Page<Pedido> findByClienteIdClienteAndEstadoNot(Long idCliente, String estadoExcluido, Pageable pageable);

    // Buscar pedidos por rango de fechas
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin AND p.estado != 'ANULADO'")
    List<Pedido> findByFechaPedidoBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // Buscar pedidos por rango de fechas con paginación
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin AND p.estado != 'ANULADO'")
    Page<Pedido> findByFechaPedidoBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Total de ventas por mes (para reporte)
    @Query("SELECT YEAR(p.fechaPedido) as anio," +
            " MONTH(p.fechaPedido) as mes," +
            " SUM(p.total) as total " +
            "FROM Pedido p " +
            "WHERE p.estado != 'ANULADO' " +
            "GROUP BY YEAR(p.fechaPedido), MONTH(p.fechaPedido) " +
            "ORDER BY YEAR(p.fechaPedido) DESC," +
            " MONTH(p.fechaPedido) DESC")
    List<Object[]> findTotalVentasPorMes();

    // Contar pedidos por estado
    long countByEstado(String estado);

    // Buscar con Specifications y paginación
    Page<Pedido> findAll(Specification<Pedido> spec, Pageable pageable);

    // Contar con Specifications
    long count(Specification<Pedido> spec);

}
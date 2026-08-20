package etctech.gestionventas.repository;

import etctech.gestionventas.dto.reporte.*;
import etctech.gestionventas.entity.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Pedido, Long> {

    /**
     * REP-01: Top 5 productos más vendidos
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.ProductoMasVendidoDTO(" +
            "p.idProducto, " +
            "p.nombre, " +
            "c.nombre, " +
            "SUM(dp.cantidad), " +
            "SUM(dp.subtotal), " +
            "p.stock) " +
            "FROM DetallePedido dp " +
            "JOIN dp.producto p " +
            "JOIN p.categoria c " +
            "WHERE dp.pedido.estado != 'ANULADO' " +
            "GROUP BY p.idProducto, p.nombre, c.nombre, p.stock " +
            "ORDER BY SUM(dp.cantidad) DESC")
    List<ProductoMasVendidoDTO> findTop5ProductosMasVendidos(Pageable pageable);

    /**
     * REP-02: Total de ventas por mes
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.VentaPorMesDTO(" +
            "YEAR(p.fechaPedido), " +
            "MONTH(p.fechaPedido), " +
            "COUNT(p.idPedido), " +
            "SUM(p.total)) " +
            "FROM Pedido p " +
            "WHERE p.estado != 'ANULADO' " +
            "GROUP BY YEAR(p.fechaPedido), MONTH(p.fechaPedido) " +
            "ORDER BY YEAR(p.fechaPedido) DESC, MONTH(p.fechaPedido) DESC")
    List<VentaPorMesDTO> findTotalVentasPorMes();

    /**
     * REP-02: Total de ventas por mes (con filtro de año)
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.VentaPorMesDTO(" +
            "YEAR(p.fechaPedido), " +
            "MONTH(p.fechaPedido), " +
            "COUNT(p.idPedido), " +
            "SUM(p.total)) " +
            "FROM Pedido p " +
            "WHERE p.estado != 'ANULADO' " +
            "AND YEAR(p.fechaPedido) = :anio " +
            "GROUP BY YEAR(p.fechaPedido), MONTH(p.fechaPedido) " +
            "ORDER BY MONTH(p.fechaPedido) ASC")
    List<VentaPorMesDTO> findTotalVentasPorMes(@Param("anio") Integer anio);

    /**
     * REP-03: Clientes con mayor monto de compra
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.ClienteTopCompraDTO(" +
            "c.idCliente, " +
            "CONCAT(c.nombres, ' ', c.apellidos), " +
            "c.email, " +
            "COUNT(p.idPedido), " +
            "SUM(p.total)) " +
            "FROM Pedido p " +
            "JOIN p.cliente c " +
            "WHERE p.estado != 'ANULADO' " +
            "AND c.estado = true " +
            "GROUP BY c.idCliente, c.nombres, c.apellidos, c.email " +
            "ORDER BY SUM(p.total) DESC")
    List<ClienteTopCompraDTO> findClientesTopCompras(Pageable pageable);

    /**
     * REP-04: Productos con stock menor a un valor definido
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.ProductoStockBajoDTO(" +
            "p.idProducto, " +
            "p.nombre, " +
            "c.nombre, " +
            "p.stock, " +
            "p.precio, " +
            "CASE WHEN p.stock < 5 THEN true ELSE false END) " +
            "FROM Producto p " +
            "JOIN p.categoria c " +
            "WHERE p.estado = true " +
            "AND p.stock < :limite " +
            "ORDER BY p.stock ASC")
    List<ProductoStockBajoDTO> findProductosStockBajo(@Param("limite") Integer limite);

    /**
     * REP-05: Pedidos registrados dentro de un rango de fechas
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.PedidoRangoFechaDTO(" +
            "p.idPedido, " +
            "CONCAT(c.nombres, ' ', c.apellidos), " +
            "c.email, " +
            "p.fechaPedido, " +
            "p.total, " +
            "p.estado) " +
            "FROM Pedido p " +
            "JOIN p.cliente c " +
            "WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin " +
            "AND p.estado != 'ANULADO' " +
            "ORDER BY p.fechaPedido DESC")
    List<PedidoRangoFechaDTO> findPedidosPorRangoFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * REP-05: Pedidos registrados dentro de un rango de fechas (con paginación)
     */
    @Query("SELECT new etctech.gestionventas.dto.reporte.PedidoRangoFechaDTO(" +
            "p.idPedido, " +
            "CONCAT(c.nombres, ' ', c.apellidos), " +
            "c.email, " +
            "p.fechaPedido, " +
            "p.total, " +
            "p.estado) " +
            "FROM Pedido p " +
            "JOIN p.cliente c " +
            "WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin " +
            "AND p.estado != 'ANULADO'")
    List<PedidoRangoFechaDTO> findPedidosPorRangoFechasPaginado(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);

    /**
     * REP-05: Contar pedidos en rango de fechas
     */
    @Query("SELECT COUNT(p) FROM Pedido p " +
            "WHERE p.fechaPedido BETWEEN :fechaInicio AND :fechaFin " +
            "AND p.estado != 'ANULADO'")
    Long countPedidosPorRangoFechas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

}
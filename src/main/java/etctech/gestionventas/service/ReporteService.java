package etctech.gestionventas.service;

import etctech.gestionventas.dto.reporte.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteService {

    /**
     * REP-01: Top 5 productos más vendidos
     */
    List<ProductoMasVendidoDTO> getTop5ProductosMasVendidos();

    /**
     * REP-02: Total de ventas por mes
     */
    List<VentaPorMesDTO> getTotalVentasPorMes();

    /**
     * REP-02: Total de ventas por mes (filtrado por año)
     */
    List<VentaPorMesDTO> getTotalVentasPorMes(Integer anio);

    /**
     * REP-03: Clientes con mayor monto de compra
     */
    List<ClienteTopCompraDTO> getClientesTopCompras();

    /**
     * REP-03: Clientes con mayor monto de compra (con límite)
     */
    List<ClienteTopCompraDTO> getClientesTopCompras(int limite);

    /**
     * REP-04: Productos con stock menor a un valor definido
     */
    List<ProductoStockBajoDTO> getProductosStockBajo(Integer limite);

    /**
     * REP-05: Pedidos registrados dentro de un rango de fechas
     */
    List<PedidoRangoFechaDTO> getPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * REP-05: Pedidos registrados dentro de un rango de fechas (con paginación y límite)
     */
    List<PedidoRangoFechaDTO> getPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, int limite);

    /**
     * REP-05: Resumen de pedidos en rango de fechas
     */
    ReporteResumenDTO getResumenPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

}
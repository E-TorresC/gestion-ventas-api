package etctech.gestionventas.controller;

import etctech.gestionventas.dto.reporte.*;
import etctech.gestionventas.dto.response.ApiResponseDTO;
import etctech.gestionventas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * REP-01: Top 5 productos más vendidos
     * GET /api/reportes/top-productos
     */
    @GetMapping("/top-productos")
    public ResponseEntity<ApiResponseDTO<List<ProductoMasVendidoDTO>>> getTop5ProductosMasVendidos() {
        List<ProductoMasVendidoDTO> reporte = reporteService.getTop5ProductosMasVendidos();
        return ResponseEntity.ok(ApiResponseDTO.success(reporte, "Top 5 productos más vendidos"));
    }

    /**
     * REP-02: Total de ventas por mes
     * GET /api/reportes/ventas-por-mes
     * GET /api/reportes/ventas-por-mes?anio=2026
     */
    @GetMapping("/ventas-por-mes")
    public ResponseEntity<ApiResponseDTO<List<VentaPorMesDTO>>> getTotalVentasPorMes(
            @RequestParam(required = false) Integer anio) {

        List<VentaPorMesDTO> reporte;
        if (anio != null) {
            reporte = reporteService.getTotalVentasPorMes(anio);
        } else {
            reporte = reporteService.getTotalVentasPorMes();
        }

        return ResponseEntity.ok(ApiResponseDTO.success(reporte, "Total de ventas por mes"));
    }

    /**
     * REP-03: Clientes con mayor monto de compra
     * GET /api/reportes/clientes-top
     * GET /api/reportes/clientes-top?limite=5
     */
    @GetMapping("/clientes-top")
    public ResponseEntity<ApiResponseDTO<List<ClienteTopCompraDTO>>> getClientesTopCompras(
            @RequestParam(defaultValue = "10") int limite) {

        List<ClienteTopCompraDTO> reporte = reporteService.getClientesTopCompras(limite);
        return ResponseEntity.ok(ApiResponseDTO.success(reporte, "Clientes con mayor monto de compra"));
    }

    /**
     * REP-04: Productos con stock menor a un valor definido
     * GET /api/reportes/stock-bajo?limite=10
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<ApiResponseDTO<List<ProductoStockBajoDTO>>> getProductosStockBajo(
            @RequestParam(defaultValue = "10") int limite) {

        List<ProductoStockBajoDTO> reporte = reporteService.getProductosStockBajo(limite);
        return ResponseEntity.ok(ApiResponseDTO.success(reporte, "Productos con stock bajo"));
    }

    /**
     * REP-05: Pedidos por rango de fechas
     * GET /api/reportes/pedidos-fecha?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-12-31T23:59:59
     * GET /api/reportes/pedidos-fecha?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-12-31T23:59:59&limite=20
     */
    @GetMapping("/pedidos-fecha")
    public ResponseEntity<ApiResponseDTO<List<PedidoRangoFechaDTO>>> getPedidosPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(required = false) Integer limite) {

        List<PedidoRangoFechaDTO> reporte;
        if (limite != null && limite > 0) {
            reporte = reporteService.getPedidosPorRangoFechas(fechaInicio, fechaFin, limite);
        } else {
            reporte = reporteService.getPedidosPorRangoFechas(fechaInicio, fechaFin);
        }

        return ResponseEntity.ok(ApiResponseDTO.success(reporte, "Pedidos por rango de fechas"));
    }

    /**
     * REP-05: Resumen de pedidos por rango de fechas
     * GET /api/reportes/pedidos-fecha/resumen?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-12-31T23:59:59
     */
    @GetMapping("/pedidos-fecha/resumen")
    public ResponseEntity<ApiResponseDTO<ReporteResumenDTO>> getResumenPedidosPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        ReporteResumenDTO resumen = reporteService.getResumenPedidosPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponseDTO.success(resumen, "Resumen de pedidos por rango de fechas"));
    }

}
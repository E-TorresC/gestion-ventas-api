package etctech.gestionventas.service.impl;

import etctech.gestionventas.dto.reporte.*;
import etctech.gestionventas.exception.BusinessException;
import etctech.gestionventas.repository.ReporteRepository;
import etctech.gestionventas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository reporteRepository;

    @Override
    public List<ProductoMasVendidoDTO> getTop5ProductosMasVendidos() {
        log.info("Generando reporte: Top 5 productos más vendidos");

        Pageable top5 = PageRequest.of(0, 5);
        List<ProductoMasVendidoDTO> resultados = reporteRepository.findTop5ProductosMasVendidos(top5);

        log.info("Reporte generado: {} productos encontrados", resultados.size());
        return resultados;
    }

    @Override
    public List<VentaPorMesDTO> getTotalVentasPorMes() {
        log.info("Generando reporte: Total de ventas por mes");

        List<VentaPorMesDTO> resultados = reporteRepository.findTotalVentasPorMes();

        log.info("Reporte generado: {} meses encontrados", resultados.size());
        return resultados;
    }

    @Override
    public List<VentaPorMesDTO> getTotalVentasPorMes(Integer anio) {
        log.info("Generando reporte: Total de ventas por mes (año: {})", anio);

        if (anio == null || anio < 2000 || anio > 2100) {
            throw new BusinessException("Año inválido. Debe estar entre 2000 y 2100");
        }

        List<VentaPorMesDTO> resultados = reporteRepository.findTotalVentasPorMes(anio);

        log.info("Reporte generado: {} meses encontrados para el año {}", resultados.size(), anio);
        return resultados;
    }

    @Override
    public List<ClienteTopCompraDTO> getClientesTopCompras() {
        log.info("Generando reporte: Clientes con mayor monto de compra");

        Pageable top10 = PageRequest.of(0, 10);
        List<ClienteTopCompraDTO> resultados = reporteRepository.findClientesTopCompras(top10);

        log.info("Reporte generado: {} clientes encontrados", resultados.size());
        return resultados;
    }

    @Override
    public List<ClienteTopCompraDTO> getClientesTopCompras(int limite) {
        log.info("Generando reporte: Clientes con mayor monto de compra (límite: {})", limite);

        if (limite < 1 || limite > 100) {
            throw new BusinessException("El límite debe estar entre 1 y 100");
        }

        Pageable pageable = PageRequest.of(0, limite);
        List<ClienteTopCompraDTO> resultados = reporteRepository.findClientesTopCompras(pageable);

        log.info("Reporte generado: {} clientes encontrados", resultados.size());
        return resultados;
    }

    @Override
    public List<ProductoStockBajoDTO> getProductosStockBajo(Integer limite) {
        log.info("Generando reporte: Productos con stock menor a {}", limite);

        if (limite == null || limite < 1) {
            throw new BusinessException("El límite de stock debe ser mayor a 0");
        }

        List<ProductoStockBajoDTO> resultados = reporteRepository.findProductosStockBajo(limite);

        log.info("Reporte generado: {} productos con stock bajo", resultados.size());
        return resultados;
    }

    @Override
    public List<PedidoRangoFechaDTO> getPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Generando reporte: Pedidos por rango de fechas ({} - {})", fechaInicio, fechaFin);

        validarRangoFechas(fechaInicio, fechaFin);

        List<PedidoRangoFechaDTO> resultados = reporteRepository.findPedidosPorRangoFechas(fechaInicio, fechaFin);

        log.info("Reporte generado: {} pedidos encontrados", resultados.size());
        return resultados;
    }

    @Override
    public List<PedidoRangoFechaDTO> getPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, int limite) {
        log.info("Generando reporte: Pedidos por rango de fechas con límite ({} - {}, límite: {})",
                fechaInicio, fechaFin, limite);

        validarRangoFechas(fechaInicio, fechaFin);

        if (limite < 1 || limite > 1000) {
            throw new BusinessException("El límite debe estar entre 1 y 1000");
        }

        Pageable pageable = PageRequest.of(0, limite);
        List<PedidoRangoFechaDTO> resultados = reporteRepository.findPedidosPorRangoFechasPaginado(
                fechaInicio, fechaFin, pageable);

        log.info("Reporte generado: {} pedidos encontrados", resultados.size());
        return resultados;
    }

    @Override
    public ReporteResumenDTO getResumenPedidosPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Generando resumen de pedidos por rango de fechas ({} - {})", fechaInicio, fechaFin);

        validarRangoFechas(fechaInicio, fechaFin);

        List<PedidoRangoFechaDTO> pedidos = reporteRepository.findPedidosPorRangoFechas(fechaInicio, fechaFin);

        if (pedidos.isEmpty()) {
            return ReporteResumenDTO.builder()
                    .totalPedidos(0L)
                    .totalVentas(BigDecimal.ZERO)
                    .promedioVenta(BigDecimal.ZERO)
                    .ventaMinima(BigDecimal.ZERO)
                    .ventaMaxima(BigDecimal.ZERO)
                    .build();
        }

        Long totalPedidos = (long) pedidos.size();
        BigDecimal totalVentas = pedidos.stream()
                .map(PedidoRangoFechaDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal promedioVenta = totalVentas.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP);

        BigDecimal ventaMinima = pedidos.stream()
                .map(PedidoRangoFechaDTO::getTotal)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal ventaMaxima = pedidos.stream()
                .map(PedidoRangoFechaDTO::getTotal)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return ReporteResumenDTO.builder()
                .totalPedidos(totalPedidos)
                .totalVentas(totalVentas)
                .promedioVenta(promedioVenta)
                .ventaMinima(ventaMinima)
                .ventaMaxima(ventaMaxima)
                .build();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private void validarRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("La fecha de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        // Validar que el rango no sea mayor a 1 año (para evitar consultas muy pesadas)
        if (fechaInicio.plusYears(1).isBefore(fechaFin)) {
            throw new BusinessException("El rango de fechas no puede ser mayor a 1 año");
        }
    }

}
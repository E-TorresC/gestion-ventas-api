package etctech.gestionventas.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteResumenDTO {

    private Long totalPedidos;
    private BigDecimal totalVentas;
    private BigDecimal promedioVenta;
    private BigDecimal ventaMinima;
    private BigDecimal ventaMaxima;

}
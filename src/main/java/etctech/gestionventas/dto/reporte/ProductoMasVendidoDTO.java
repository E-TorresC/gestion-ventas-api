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
public class ProductoMasVendidoDTO {

    private Long idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private Long totalUnidadesVendidas;
    private BigDecimal totalVentas;
    private Integer stockActual;

}
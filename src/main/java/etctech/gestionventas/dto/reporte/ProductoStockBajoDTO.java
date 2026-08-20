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
public class ProductoStockBajoDTO {

    private Long idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private Integer stockActual;
    private BigDecimal precio;
    private Boolean necesitaReposicion;

    public Boolean getNecesitaReposicion() {
        return stockActual != null && stockActual < 5;
    }

}
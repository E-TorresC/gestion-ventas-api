package etctech.gestionventas.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoFiltroDTO {

    private String nombre;
    private String nombreCategoria;
    private BigDecimal precioMinimo;
    private BigDecimal precioMaximo;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Long idCategoria;
    private Boolean estado;

}
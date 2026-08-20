package etctech.gestionventas.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoFiltroDTO {

    private Long idCliente;
    private String nombreCliente;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal totalMinimo;
    private BigDecimal totalMaximo;
    private Boolean incluirAnulados = false;

}
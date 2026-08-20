package etctech.gestionventas.dto.reporte;

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
public class PedidoRangoFechaDTO {

    private Long idPedido;
    private String clienteNombre;
    private String clienteEmail;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private String estado;



}
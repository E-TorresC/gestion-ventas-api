package etctech.gestionventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {

    private Long idPedido;
    private ClienteResponseDTO cliente;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private String estado;
    private List<DetallePedidoResponseDTO> detalles;

}
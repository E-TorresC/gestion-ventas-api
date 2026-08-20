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
public class ClienteTopCompraDTO {

    private Long idCliente;
    private String nombreCompleto;
    private String email;
    private Long cantidadPedidos;
    private BigDecimal totalCompras;

}
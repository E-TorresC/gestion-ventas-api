package etctech.gestionventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    private Long idCliente;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private Boolean estado;
    private LocalDateTime fechaRegistro;
    private String nombreCompleto;

}
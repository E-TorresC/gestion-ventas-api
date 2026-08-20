package etctech.gestionventas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    private Long idProducto;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private Boolean estado;
    private CategoriaResponseDTO categoria;

}
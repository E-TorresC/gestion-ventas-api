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
public class VentaPorMesDTO {

    private Integer anio;
    private Integer mes;
    private String nombreMes;
    private Long cantidadPedidos;
    private BigDecimal totalVentas;

    public VentaPorMesDTO(
            Integer anio,
            Integer mes,
            Long cantidadPedidos,
            BigDecimal totalVentas) {

        this.anio = anio;
        this.mes = mes;
        this.cantidadPedidos = cantidadPedidos;
        this.totalVentas = totalVentas;
    }

    public String getNombreMes() {
        if (mes == null) return null;
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }

}
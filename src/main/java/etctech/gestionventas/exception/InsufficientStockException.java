package etctech.gestionventas.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends BusinessException {

    private final Long idProducto;
    private final String nombreProducto;
    private final Integer stockDisponible;
    private final Integer cantidadSolicitada;

    public InsufficientStockException(Long idProducto, String nombreProducto,
                                      Integer stockDisponible, Integer cantidadSolicitada) {
        super(String.format(
                "Stock insuficiente para el producto '%s' (ID: %d). Stock disponible: %d, Cantidad solicitada: %d",
                nombreProducto, idProducto, stockDisponible, cantidadSolicitada
        ), "INSUFFICIENT_STOCK");
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

}
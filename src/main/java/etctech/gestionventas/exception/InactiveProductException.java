package etctech.gestionventas.exception;

import lombok.Getter;

@Getter
public class InactiveProductException extends BusinessException {

    private final Long idProducto;
    private final String nombreProducto;

    public InactiveProductException(Long idProducto, String nombreProducto) {
        super(String.format(
                "No se puede vender el producto '%s' (ID: %d) porque se encuentra inactivo",
                nombreProducto, idProducto
        ), "INACTIVE_PRODUCT");
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
    }

}
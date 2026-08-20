package etctech.gestionventas.exception;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends BusinessException {

    private final String estadoActual;
    private final String nuevoEstado;

    public InvalidStateTransitionException(String estadoActual, String nuevoEstado) {
        super(String.format(
                "No se puede cambiar el estado del pedido de '%s' a '%s'",
                estadoActual, nuevoEstado
        ), "INVALID_STATE_TRANSITION");
        this.estadoActual = estadoActual;
        this.nuevoEstado = nuevoEstado;
    }

}
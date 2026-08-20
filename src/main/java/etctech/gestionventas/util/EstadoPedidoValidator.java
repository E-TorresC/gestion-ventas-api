package etctech.gestionventas.util;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public final class EstadoPedidoValidator {

    private static final Map<String, Set<String>> TRANSICIONES_PERMITIDAS = new HashMap<>();

    static {
        // Definir transiciones permitidas
        TRANSICIONES_PERMITIDAS.put(Constants.ESTADO_PENDIENTE,
                Set.of(Constants.ESTADO_CONFIRMADO, Constants.ESTADO_ANULADO));
        TRANSICIONES_PERMITIDAS.put(Constants.ESTADO_CONFIRMADO,
                Set.of(Constants.ESTADO_ENVIADO, Constants.ESTADO_ANULADO));
        TRANSICIONES_PERMITIDAS.put(Constants.ESTADO_ENVIADO,
                Set.of(Constants.ESTADO_ENTREGADO));
        TRANSICIONES_PERMITIDAS.put(Constants.ESTADO_ENTREGADO,
                Set.of());
        TRANSICIONES_PERMITIDAS.put(Constants.ESTADO_ANULADO,
                Set.of());
    }

    private EstadoPedidoValidator() {
        // Constructor privado
    }

    public static boolean isTransitionValid(String estadoActual, String nuevoEstado) {
        if (!TRANSICIONES_PERMITIDAS.containsKey(estadoActual)) {
            return false;
        }
        return TRANSICIONES_PERMITIDAS.get(estadoActual).contains(nuevoEstado);
    }

    public static Set<String> getEstadosPermitidos(String estadoActual) {
        return TRANSICIONES_PERMITIDAS.getOrDefault(estadoActual, Set.of());
    }

}
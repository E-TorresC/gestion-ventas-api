package etctech.gestionventas.util;

public final class Constants {

    private Constants() {
        // Constructor privado para evitar instanciación
    }

    // Estados de Pedido
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_CONFIRMADO = "CONFIRMADO";
    public static final String ESTADO_ENVIADO = "ENVIADO";
    public static final String ESTADO_ENTREGADO = "ENTREGADO";
    public static final String ESTADO_ANULADO = "ANULADO";

    // Lista de estados válidos para validaciones
    public static final String[] ESTADOS_VALIDOS = {
            ESTADO_PENDIENTE, ESTADO_CONFIRMADO, ESTADO_ENVIADO, ESTADO_ENTREGADO, ESTADO_ANULADO
    };

    // Mensajes de Error
    public static final String MENSAJE_RECURSO_NO_ENCONTRADO = "Recurso no encontrado con ID: ";
    public static final String MENSAJE_CLIENTE_NO_ENCONTRADO = "Cliente no encontrado con ID: ";
    public static final String MENSAJE_CATEGORIA_NO_ENCONTRADA = "Categoría no encontrada con ID: ";
    public static final String MENSAJE_PRODUCTO_NO_ENCONTRADO = "Producto no encontrado con ID: ";
    public static final String MENSAJE_PEDIDO_NO_ENCONTRADO = "Pedido no encontrado con ID: ";
    public static final String MENSAJE_STOCK_INSUFICIENTE = "Stock insuficiente para el producto: ";
    public static final String MENSAJE_PRODUCTO_INACTIVO = "El producto no se encuentra activo: ";
    public static final String MENSAJE_PEDIDO_YA_ANULADO = "El pedido ya se encuentra anulado: ";
    public static final String MENSAJE_ESTADO_INVALIDO = "Estado de pedido inválido: ";

    // Validaciones
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

}
package cl.kiosko.ms_inventario.Exception;

/**
 * Excepción lanzada cuando hay un intento de venta o descuento de inventario
 * y el stock actual es insuficiente.
 */
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String message) {
        super(message);
    }
}

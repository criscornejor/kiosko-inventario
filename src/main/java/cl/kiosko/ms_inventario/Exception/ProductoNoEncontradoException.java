package cl.kiosko.ms_inventario.Exception;

/**
 * Excepción lanzada cuando no se encuentra un producto solicitado.
 */
public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String message) {
        super(message);
    }
}

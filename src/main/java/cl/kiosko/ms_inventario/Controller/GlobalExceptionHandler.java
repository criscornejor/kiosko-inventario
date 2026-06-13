package cl.kiosko.ms_inventario.Controller;

import cl.kiosko.ms_inventario.DTO.ExceptionDTO;
import cl.kiosko.ms_inventario.Exception.ProductoNoEncontradoException;
import cl.kiosko.ms_inventario.Exception.CategoriaNoEncontradaException;
import cl.kiosko.ms_inventario.Exception.StockInsuficienteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Manejador global de excepciones para toda la aplicación.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NoSuchElementException.class, ProductoNoEncontradoException.class, CategoriaNoEncontradaException.class})
    public ResponseEntity<ExceptionDTO> handleNotFound(Exception ex){
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.NOT_FOUND,
                ex
        );
        return new ResponseEntity<>(exceptionDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * Violaciones de integridad de datos (FK en uso, valores duplicados, etc.).
     * Spring envuelve los errores SQL del driver en DataIntegrityViolationException,
     * por lo que este handler cubre ambos casos sin exponer el mensaje SQL crudo.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDTO> handleConflict(DataIntegrityViolationException ex){
        log.warn("Violación de integridad de datos: {}", ex.getMessage());
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.CONFLICT,
                "La operación viola una restricción de integridad (registro duplicado o referenciado por otros datos)"
        );
        return new ResponseEntity<>(exceptionDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ExceptionDTO> handleStockInsuficiente(StockInsuficienteException ex){
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.CONFLICT,
                ex
        );
        return new ResponseEntity<>(exceptionDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Validaciones sobre parámetros de método (@RequestParam, @PathVariable),
     * por ejemplo la cantidad mínima en descontar-stock.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionDTO> handleParameterValidation(HandlerMethodValidationException ex) {
        String mensaje = ex.getAllErrors().stream()
                .map(error -> String.valueOf(error.getDefaultMessage()))
                .reduce((a, b) -> a + "; " + b)
                .orElse("Parámetros inválidos");
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST, mensaje);
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleGlobalException(Exception ex){
        log.error("Error interno no controlado", ex);
        ExceptionDTO exceptionDTO = new ExceptionDTO(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor"
        );
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

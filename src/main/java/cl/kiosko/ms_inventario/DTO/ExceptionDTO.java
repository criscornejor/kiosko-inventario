package cl.kiosko.ms_inventario.DTO;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ExceptionDTO {
    private int code;
    private String type;
    private String date;
    private String message;

    public ExceptionDTO(HttpStatus httpStatus, Exception exception) {
        this(httpStatus, exception.getMessage());
    }

    public ExceptionDTO(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.type = httpStatus.getReasonPhrase();
        this.date = (new Date()).toString();
        this.message = message;
    }
}

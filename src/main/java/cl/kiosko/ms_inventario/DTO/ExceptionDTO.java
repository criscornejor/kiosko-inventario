package cl.kiosko.ms_inventario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
public class ExceptionDTO {
    private int code;
    private String type;
    private String date;
    private String message;

    public ExceptionDTO(HttpStatus httpStatus, Exception exception) {
        this.code = httpStatus.value();
        this.type = httpStatus.getReasonPhrase();
        this.date = (new Date()).toString();
        this.message = exception.getMessage();
    }
}

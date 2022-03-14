package it.si2001.rentalcar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -1319628164388929379L;

    private final String error;

    public CustomException(String error) {
        super(String.format("%s", error));
        this.error = error;
    }

    public String getFieldName() {
        return error;
    }

}

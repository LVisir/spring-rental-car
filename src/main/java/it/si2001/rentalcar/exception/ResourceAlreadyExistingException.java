package it.si2001.rentalcar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Exception when an operation try to insert an existing entity
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceAlreadyExistingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5717561261584718945L;
    private String resourceName;
    private String fieldName;
    private Object fiedlValue;

    public ResourceAlreadyExistingException(String resourceName, String fieldName, Object fiedlValue) {
        super(String.format("%s with %s %s already exists", resourceName, fieldName, fiedlValue));
        this.fieldName = fieldName;
        this.fiedlValue = fiedlValue;
        this.resourceName = resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFiedlValue() {
        return fiedlValue;
    }

    public String getResourceName() {
        return resourceName;
    }
}

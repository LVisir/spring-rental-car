package it.si2001.rentalcar.exception;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -3350813123491614584L;

    private String resourceName;
    private String fieldName;
    private Object fiedlValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fiedlValue) {
        super(String.format("%s data with %s %s not found", resourceName, fieldName, fiedlValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fiedlValue = fiedlValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFiedlValue() {
        return fiedlValue;
    }
}

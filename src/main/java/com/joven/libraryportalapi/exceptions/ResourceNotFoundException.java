package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s: %s does not exist", entityName, fieldName, fieldValue));
    }
}

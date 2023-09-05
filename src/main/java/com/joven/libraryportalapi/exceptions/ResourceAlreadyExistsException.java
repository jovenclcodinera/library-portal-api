package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s: %s already exists", entityName, fieldName, fieldValue));
    }
}

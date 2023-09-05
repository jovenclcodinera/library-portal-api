package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterValueException extends RuntimeException {
    public InvalidParameterValueException(String paramName, Object paramValue) {
        super(String.format("Invalid Value: %s for Parameter: %s", paramValue, paramName));
    }
}

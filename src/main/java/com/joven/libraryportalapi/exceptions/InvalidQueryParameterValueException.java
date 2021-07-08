package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidQueryParameterValueException extends RuntimeException {

    public InvalidQueryParameterValueException(String paramName, Object paramValue) {
        super(String.format("Invalid value: %s for Query Parameter: %s", paramValue, paramName));
    }
}

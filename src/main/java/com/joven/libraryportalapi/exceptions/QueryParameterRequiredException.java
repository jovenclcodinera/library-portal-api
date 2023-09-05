package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QueryParameterRequiredException extends RuntimeException {
    public QueryParameterRequiredException(String message) {
        super(message);
    }
}

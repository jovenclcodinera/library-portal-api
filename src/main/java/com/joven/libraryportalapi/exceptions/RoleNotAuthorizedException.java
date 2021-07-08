package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleNotAuthorizedException extends RuntimeException {
    public RoleNotAuthorizedException(String role) {
        super(String.format("User's role as %s is not authorized to proceed with this request", role));
    }
}

package com.joven.libraryportalapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyBorrowedException extends RuntimeException {
    public AlreadyBorrowedException(String username, String userRole, String bookTitle) {
        super(String.format("%s: %s has already borrowed the Book: %s", userRole, username, bookTitle));
    }
}

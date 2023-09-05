package com.joven.libraryportalapi.exceptions;

public class AuthorOnlyException extends RuntimeException {
    public AuthorOnlyException() {
        super("Only a Librarian or the Author of this book can update it");
    }
}

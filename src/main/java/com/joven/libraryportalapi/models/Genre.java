package com.joven.libraryportalapi.models;

import lombok.Data;

import java.util.List;

@Data
public class Genre {

    private Long id;
    private String name;
    private List<Book> books;

    public Genre(String name) {
        this.name = name;
    }
}

package com.joven.libraryportalapi.models;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
public class Book {

    private Long id;
    private String title;
    private String author;
    private LocalDate written_date;
    private String publisher;
    private LocalDate publication_date;
    private Integer pages;
    private String language;
    private Integer copies;
    private List<String> genres;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Timestamp deleted_at;
}

package com.joven.libraryportalapi.models;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class BorrowedBook {

    private Long id;
    private Long book_id;
    private Long borrower_id;
    private LocalDateTime borrowed_date;
    private LocalDateTime expiration_date;
    private LocalDateTime return_date;
    private Integer copies;
    private Timestamp created_at;
    private Timestamp updated_at;
    private Timestamp deleted_at;
}

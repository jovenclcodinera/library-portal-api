package com.joven.libraryportalapi.controllers;

import com.joven.libraryportalapi.models.BorrowedBook;
import com.joven.libraryportalapi.services.BorrowedBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BorrowedBooksController {

    @Autowired
    private BorrowedBooksService borrowedBooksService;

    @PostMapping("{id}/borrow")
    public ResponseEntity<Map<String, Object>> borrow(@PathVariable("id") Long id) {
        BorrowedBook savedBorrowedBook = borrowedBooksService.save(id);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "Book was successfully borrowed");
        map.put("data", savedBorrowedBook);

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("borrow")
    public ResponseEntity<?> index(@RequestParam(required = false) Integer page) {
        return new ResponseEntity<>(borrowedBooksService.getAllBorrowedBooks(page != null ? page : 0), HttpStatus.OK);
    }

    @GetMapping("borrow/{id}")
    public ResponseEntity<?> show(@PathVariable("id") Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", borrowedBooksService.findById(id));
        map.put("message", "Borrowed Book fetched successfully");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("borrow/{borrowedBookId}")
    public ResponseEntity<?> update(@PathVariable("borrowedBookId") Long borrowedBookId, @RequestBody BorrowedBook borrowedBook) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", borrowedBooksService.update(borrowedBookId, borrowedBook));
        map.put("message", "Borrowed Book successfully updated");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("borrow/{borrowedBookId}")
    public ResponseEntity<?> destroy(@PathVariable("borrowedBookId") Long borrowedBookId) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Borrowed Book successfully deleted");
        borrowedBooksService.destroy(borrowedBookId);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("{id}/return")
    public ResponseEntity<?> returnBorrowedBook(@PathVariable("id") Long bookId) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Borrowed Book successfully returned");
        map.put("data", borrowedBooksService.returnBorrowedBook(bookId));

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}

package com.joven.libraryportalapi.controllers;

import com.joven.libraryportalapi.exceptions.AuthorOnlyException;
import com.joven.libraryportalapi.exceptions.QueryParameterRequiredException;
import com.joven.libraryportalapi.exceptions.RoleNotAuthorizedException;
import com.joven.libraryportalapi.models.Book;
import com.joven.libraryportalapi.models.Page;
import com.joven.libraryportalapi.models.User;
import com.joven.libraryportalapi.services.AuthenticationsService;
import com.joven.libraryportalapi.services.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    @Autowired
    private BooksService booksService;

    @Autowired
    private AuthenticationsService authenticationsService;

    @PostMapping()
    public ResponseEntity<Map<String, Object>> save(@RequestBody Book book) {
        User user = authenticationsService.getAuthenticatedUser();
        if (! Arrays.asList("LIBRARIAN", "AUTHOR").contains(user.getRole())) {
            throw new RoleNotAuthorizedException(user.getRole());
        }

        Map<String, Object> map = new HashMap<>();
        book.setAuthor(user.getUsername());
        map.put("data", booksService.save(book));
        map.put("message", String.format("Book: %s successfully created", book.getTitle()));

        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> map = new HashMap<>();
        List<Book> books = booksService.findAll();
        map.put("message", books.size() > 0 ? "All available books fetched successfully" : "No books were available at the moment");
        map.put("data", books);
        map.put("total", books.size());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Map<String, Object>> show(@PathVariable("id") Long id) {
        Map<String, Object> map = new HashMap<>();
        Book book = booksService.findById(id);
        map.put("message", String.format("%s was fetched successfully", book.getTitle()));
        map.put("data", book);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("id") Long id, @RequestBody Book book) {
        User user = authenticationsService.getAuthenticatedUser();

        if (! Arrays.asList("LIBRARIAN", "AUTHOR").contains(user.getRole())) {
            throw new RoleNotAuthorizedException(user.getRole());
        } else if (! booksService.isTheBookAuthor(id, user.getUsername()) || ! user.getRole().equals("LIBRARIAN")) {
            throw new AuthorOnlyException();
        }

        Map<String, Object> map = new HashMap<>();
        book.setId(id);
        Book updatedBook = booksService.update(book);
        map.put("data", updatedBook);
        map.put("message", String.format("Book: %s successfully updated", updatedBook.getTitle()));

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, Object>> destroy(@PathVariable("id") Long id) {
        User user = authenticationsService.getAuthenticatedUser();
        if (! user.getRole().equals("LIBRARIAN")) {
            throw new RoleNotAuthorizedException(user.getRole());
        }

        Map<String, Object> map = new HashMap<>();
        Book book = booksService.findById(id);
        map.put("message", String.format("Book: %s successfully deleted", book.getTitle()));
        map.put("data", null);
        booksService.destroy(id);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<?> search(@RequestParam Map<String, Object> queryParams) {
        if (queryParams.isEmpty()) {
            throw new QueryParameterRequiredException("At least 1 search parameter is required");
        }

        PagedListHolder<Book> pagedListHolder = booksService.searchByQueries(queryParams);
        Page page = new Page(pagedListHolder);
        page.setMessage(pagedListHolder.getPageList().size() > 0 ? "Books were fetched successfully" : "No books were fetched based on your queries");

        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}

package com.joven.libraryportalapi.services;

import com.joven.libraryportalapi.exceptions.InvalidQueryParameterValueException;
import com.joven.libraryportalapi.exceptions.ResourceAlreadyExistsException;
import com.joven.libraryportalapi.exceptions.ResourceNotFoundException;
import com.joven.libraryportalapi.models.Book;
import com.joven.libraryportalapi.models.Genre;
import com.joven.libraryportalapi.repositories.BooksRepository;
import com.joven.libraryportalapi.repositories.BorrowedBooksRepository;
import com.joven.libraryportalapi.repositories.GenreBookRepository;
import com.joven.libraryportalapi.repositories.GenresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BooksService {

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private GenreBookRepository genreBookRepository;

    @Autowired
    private BorrowedBooksRepository borrowedBooksRepository;

    @Autowired
    private AuthenticationsService authenticationsService;

    public Book findById(long id) {
        Book book = booksRepository.findById(id);
        if (book == null) {
            throw new ResourceNotFoundException("Books", "Id", id);
        }

        return addGenresToBook(book);
    }

    public Book addGenresToBook(Book book) {
        List<Long> genreIds = genreBookRepository.getAllByBook(book.getId());
        List<String> genreNames = new ArrayList<>();
        genreIds.forEach(id -> {
            Genre genre = genresRepository.findById(id);
            genreNames.add(genre.getName());
        });
        book.setGenres(genreNames);

        return book;
    }

    public Book findByTitle(String title) {
        Book book = booksRepository.findByTitle(title);
        if (book == null) {
            throw new ResourceNotFoundException("Books", "Title", title);
        }

        return book;
    }

    public Book save(Book book) {
        if (booksRepository.findByTitle(book.getTitle().trim()) != null) {
            throw new ResourceAlreadyExistsException("Book", "Title", book.getTitle());
        }

        booksRepository.save(book);
        long savedBookId = booksRepository.lastInsertId();
        if (! book.getGenres().isEmpty()) {
            book.getGenres().forEach(genreName -> {
                Genre genre = genresRepository.findByName(genreName.trim());
                if (genre == null) {
                    genresRepository.save(new Genre(genreName.trim()));
                    long savedGenreId = genresRepository.lastInsertId();
                    genreBookRepository.save(savedGenreId, savedBookId);
                } else {
                    if (genreBookRepository.checkIfExists(genre.getId(), savedBookId) <= 0) {
                        genreBookRepository.save(genre.getId(), savedBookId);
                    }
                }
            });
        }

        Book fetchedBook = booksRepository.findById(savedBookId);
        fetchedBook.setGenres(book.getGenres());

        return fetchedBook;
    }

    public List<Book> findAll() {
        List<Book> books = booksRepository.findAll();
        books.forEach(this::addGenresToBook);

        return books;
    }

    public Book update(Book book) {
        Book updatedBook = this.findById(book.getId());

        if (book.getTitle() != null) updatedBook.setTitle(book.getTitle());
        if (book.getAuthor() != null) updatedBook.setAuthor(book.getAuthor());
        if (book.getWritten_date() != null) updatedBook.setWritten_date(book.getWritten_date());
        if (book.getPublisher() != null) updatedBook.setPublisher(book.getPublisher());
        if (book.getPublication_date() != null) updatedBook.setPublication_date(book.getPublication_date());
        if (book.getPages() != null) updatedBook.setPages(book.getPages());
        if (book.getLanguage() != null) updatedBook.setLanguage(book.getLanguage());
        if (book.getCopies() != null) updatedBook.setCopies(book.getCopies());

        if (booksRepository.update(updatedBook) && book.getGenres() != null) {
            List<String> oldGenreNames = updatedBook.getGenres();
            book.getGenres().forEach(genreName -> {
                Genre genre = genresRepository.findByName(genreName.trim());
                if (genre == null) {
                    genre = new Genre(genreName.trim());
                    genresRepository.save(genre);
                    Long genreId = genresRepository.lastInsertId();
                    genreBookRepository.save(genreId, book.getId());
                } else {
                    if (genreBookRepository.checkIfExists(genre.getId(), book.getId()) <= 0) {
                        genreBookRepository.save(genre.getId(), book.getId());
                    }
                }

                oldGenreNames.remove(genreName);
            });

            if (oldGenreNames.size() > 0) {
                oldGenreNames.forEach(genreName -> {
                    Genre genre = genresRepository.findByName(genreName.trim());
                    genreBookRepository.delete(genre.getId(), book.getId());
                });
            }

            updatedBook.setGenres(book.getGenres());
        }

        return updatedBook;
    }

    public void destroy(long id) {
        this.findById(id);

        if (borrowedBooksRepository.checkIfBookAlreadyBorrowed(id, authenticationsService.getAuthenticatedUser().getId()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete Book entity. Some copies are still borrowed");
        }

        genreBookRepository.deleteAllByBook(id);
        booksRepository.delete(id);
    }

    public Book arrangeSearchQueries(Map<String, Object> queryParams) {
        Book book = new Book();

        if (queryParams.containsKey("title")) book.setTitle((String) queryParams.get("title"));
        if (queryParams.containsKey("author")) book.setAuthor((String) queryParams.get("author"));
        if (queryParams.containsKey("written_date")) book.setWritten_date((LocalDate) queryParams.get("written_date"));
        if (queryParams.containsKey("publisher")) book.setPublisher((String) queryParams.get("publisher"));
        if (queryParams.containsKey("publication_date")) book.setPublication_date((LocalDate) queryParams.get("publication_date"));
        if (queryParams.containsKey("pages")) book.setPages((Integer) queryParams.get("pages"));
        if (queryParams.containsKey("language")) book.setLanguage((String) queryParams.get("language"));

        return book;
    }

    public PagedListHolder<Book> searchByQueries(Map<String, Object> queryParams) {
        Book bookWithQueries = arrangeSearchQueries(queryParams);
        List<Book> books = new ArrayList<>();
        booksRepository.search(bookWithQueries).forEach(book -> books.add(addGenresToBook(book)));

        int page = 1;
        PagedListHolder<Book> bookPagedListHolder;
        if (queryParams.get("page") == null) {
            bookPagedListHolder = paginateList(books, page);
        } else if (queryParams.get("page") instanceof String && ((String) queryParams.get("page")).matches(".*\\d.*")) {
            page = Integer.parseInt((String) queryParams.get("page"));
            bookPagedListHolder = paginateList(books, page);
        } else {
            throw new InvalidQueryParameterValueException("page", queryParams.get("page"));
        }

        return bookPagedListHolder;
    }

    public PagedListHolder<Book> paginateList(List<Book> books, int page) {
        PagedListHolder<Book> bookPagedListHolder = new PagedListHolder<>();
        bookPagedListHolder.setSource(books);
        bookPagedListHolder.setPage(page - 1);

        return bookPagedListHolder;
    }

    public boolean isTheBookAuthor(Long id, String authorName) {
        return findById(id).getAuthor().equals(authorName);
    }
}

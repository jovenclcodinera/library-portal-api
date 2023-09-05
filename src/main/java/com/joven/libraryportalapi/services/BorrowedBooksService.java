package com.joven.libraryportalapi.services;

import com.joven.libraryportalapi.exceptions.*;
import com.joven.libraryportalapi.models.Book;
import com.joven.libraryportalapi.models.BorrowedBook;
import com.joven.libraryportalapi.models.Page;
import com.joven.libraryportalapi.models.User;
import com.joven.libraryportalapi.repositories.BooksRepository;
import com.joven.libraryportalapi.repositories.BorrowedBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BorrowedBooksService {

    @Autowired
    private BorrowedBooksRepository borrowedBooksRepository;

    @Autowired
    private BooksService booksService;

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private AuthenticationsService authenticationsService;

    public BorrowedBook validateFieldsForInsert(Long bookId) {
        Book book = booksService.findById(bookId);
        User user = authenticationsService.getAuthenticatedUser();

        if (book.getCopies() == 0) {
            throw new OutOfStockException("Book", "Title", book.getTitle());
        }

        if (borrowedBooksRepository.checkIfBookAlreadyBorrowed(book.getId(), user.getId()) > 0) {
            throw new AlreadyBorrowedException(user.getUsername(), user.getRole(), book.getTitle());
        }

        book.setCopies(book.getCopies() - 1);
        booksRepository.update(book);

        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setBook_id(book.getId());
        borrowedBook.setBorrower_id(user.getId());
        borrowedBook.setBorrowed_date(LocalDateTime.now());
        borrowedBook.setExpiration_date(LocalDateTime.now().plusDays(10));

        return borrowedBook;
    }

    public BorrowedBook save(Long id) {
        BorrowedBook borrowedBook = this.validateFieldsForInsert(id);
        borrowedBooksRepository.save(borrowedBook);
        Long insertedId = borrowedBooksRepository.lastInsertId();
        return borrowedBooksRepository.findById(insertedId);
    }

    public Page getAllBorrowedBooks(Integer page) {
        List<BorrowedBook> borrowedBooks = borrowedBooksRepository.getAll();
        PagedListHolder<BorrowedBook> pagedListHolder = new PagedListHolder<>();
        pagedListHolder.setSource(borrowedBooks);
        pagedListHolder.setPage(page);

        Page p = new Page(pagedListHolder);
        p.setMessage(borrowedBooks.size() > 0 ? "Borrowed Books were fetched successfully" : "No books were borrowed at the moment");
        return p;
    }

    public BorrowedBook findById(Long id) {
        BorrowedBook borrowedBook = borrowedBooksRepository.findById(id);
        if (borrowedBook == null) {
            throw new ResourceNotFoundException("Borrowed Book", "Id", id);
        }

        return borrowedBook;
    }

    public BorrowedBook validateFieldsForUpdate(BorrowedBook borrowedBook) {
        BorrowedBook oldBorrowedBook = this.findById(borrowedBook.getId());

        if (borrowedBook.getBorrowed_date() != null && borrowedBook.getBorrowed_date().isBefore(LocalDateTime.now())) {
            oldBorrowedBook.setBorrowed_date(borrowedBook.getBorrowed_date());
            oldBorrowedBook.setExpiration_date(borrowedBook.getBorrowed_date().plusDays(10));
        } else if (borrowedBook.getExpiration_date() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiration Date cannot be updated and only dependent on Borrow Date");
        }

        if (borrowedBook.getReturn_date() != null) {
            if (borrowedBook.getReturn_date().isBefore(borrowedBook.getBorrowed_date())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Return date must be after Borrow Date: %s", borrowedBook.getBorrowed_date()));
            } else {
                oldBorrowedBook.setReturn_date(borrowedBook.getReturn_date());
            }
        }

        if (borrowedBook.getCopies() != null) {
            Book book = booksRepository.findById(oldBorrowedBook.getBook_id());
            if (borrowedBook.getCopies() <= 0) {
                throw new InvalidParameterValueException("Copies", borrowedBook.getCopies());
            } else if (borrowedBook.getCopies() > (book.getCopies() + oldBorrowedBook.getCopies())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of copies to borrow must not exceed number of available copies");
            } else {
                book.setCopies((book.getCopies() + oldBorrowedBook.getCopies()) - borrowedBook.getCopies());
                booksRepository.update(book);

                oldBorrowedBook.setCopies(borrowedBook.getCopies());
            }
        }

        return oldBorrowedBook;
    }

    public BorrowedBook update(Long id, BorrowedBook borrowedBook) {
        User user = authenticationsService.getAuthenticatedUser();
        if (! user.getRole().equals("LIBRARIAN")) {
            throw new RoleNotAuthorizedException(user.getRole());
        }

        borrowedBook.setId(id);
        BorrowedBook oldBorrowedBook = this.validateFieldsForUpdate(borrowedBook);
        borrowedBooksRepository.update(oldBorrowedBook);

        return oldBorrowedBook;
    }

    public void destroy(Long id) {
        User user = authenticationsService.getAuthenticatedUser();
        if (! user.getRole().equals("LIBRARIAN")) {
            throw new RoleNotAuthorizedException(user.getRole());
        }

        BorrowedBook borrowedBook = this.findById(id);
        if (borrowedBook.getReturn_date() == null) {
            Book book = booksService.findById(borrowedBook.getBook_id());
            book.setCopies(book.getCopies() + borrowedBook.getCopies());
            booksRepository.update(book);
        }

        borrowedBooksRepository.destroy(id);
    }

    public BorrowedBook returnBorrowedBook(Long bookId) {
        User user = authenticationsService.getAuthenticatedUser();
        BorrowedBook borrowedBook = borrowedBooksRepository.findByBookIdAndBorrowerId(bookId, user.getId());
        if (borrowedBook == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("User: %s has no record of Borrowing book with Id: %s", user.getUsername(), bookId));
        }
        if (borrowedBook.getReturn_date() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book is already returned");
        }

        borrowedBooksRepository.returnBook(bookId, user.getId());

        return borrowedBooksRepository.findByBookIdAndBorrowerId(bookId, user.getId());
    }
}

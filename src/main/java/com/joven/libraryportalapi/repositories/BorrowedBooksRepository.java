package com.joven.libraryportalapi.repositories;

import com.joven.libraryportalapi.models.BorrowedBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BorrowedBooksRepository {

    @Select("SELECT COUNT(*) FROM borrowed_books WHERE book_id = #{bookId} AND borrower_id = #{borrowerId} AND return_date IS NULL AND deleted_at IS NULL")
    int checkIfBookAlreadyBorrowed(Long bookId, Long borrowerId);

    @Insert("INSERT INTO borrowed_books (book_id, borrower_id, borrowed_date, expiration_date) VALUES (#{book_id}, #{borrower_id}, #{borrowed_date}, #{expiration_date})")
    void save(BorrowedBook borrowedBook);

    @Select("SELECT last_insert_id()")
    long lastInsertId();

    @Select("SELECT * FROM borrowed_books WHERE id = #{id} AND deleted_at IS NULL")
    BorrowedBook findById(Long id);

    @Select("SELECT * FROM borrowed_books WHERE deleted_at IS NULL")
    List<BorrowedBook> getAll();

    @Update("UPDATE borrowed_books SET borrowed_date = #{borrowed_date}, expiration_date = #{expiration_date}, return_date = #{return_date}, copies = #{copies}")
    void update(BorrowedBook borrowedBook);

    @Update("UPDATE borrowed_books SET deleted_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void destroy(Long id);

    @Select("SELECT * FROM borrowed_books WHERE book_id = #{bookId} AND borrower_id = #{borrowerId} AND return_date IS NULL AND deleted_at IS NULL")
    BorrowedBook findByBookIdAndBorrowerId(Long bookId, Long borrowerId);

    @Update("UPDATE borrowed_books SET return_date = CURRENT_TIMESTAMP WHERE book_id = #{bookId} AND borrower_id = #{borrowerId} AND deleted_at IS NULL")
    void returnBook(Long bookId, Long borrowerId);
}
package com.joven.libraryportalapi.repositories;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GenreBookRepository {

    @Insert("INSERT INTO genre_book (genre_id, book_id) VALUES (#{genreId}, #{bookId})")
    long save(Long genreId, Long bookId);

    @Select("SELECT COUNT(*) FROM genre_book WHERE genre_id = #{genreId} AND book_id = #{bookId}")
    long checkIfExists(Long genreId, Long bookId);

    @Select("SELECT * FROM genre_book WHERE book_id = #{bookId}")
    List<Long> getAllByBook(long bookId);

    @Delete("DELETE FROM genre_book WHERE genre_id = #{genreId} AND book_id = #{bookId}")
    void delete(Long genreId, Long bookId);

    @Delete("DELETE FROM genre_book WHERE book_id = #{bookId}")
    void deleteAllByBook(Long bookId);
}

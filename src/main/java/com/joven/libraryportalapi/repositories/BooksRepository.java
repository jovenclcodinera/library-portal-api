package com.joven.libraryportalapi.repositories;

import com.joven.libraryportalapi.models.Book;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BooksRepository {

    @Insert("INSERT INTO books (title, author, written_date, publisher, publication_date, pages, language, copies) VALUES (#{title}, #{author}, #{written_date}, #{publisher}, #{publication_date}, #{pages}, #{language}, #{copies})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long save(Book book);

    @Select("SELECT last_insert_id()")
    long lastInsertId();

    @Select("SELECT * FROM books WHERE id = #{id} AND deleted_at IS NULL")
    Book findById(long id);

    @Select("SELECT * FROM books WHERE title = #{title} AND deleted_at IS NULL")
    Book findByTitle(String title);

    @Select("SELECT * FROM books WHERE deleted_at IS NULL")
    List<Book> findAll();

    @Update("UPDATE books SET title = #{title}, author = #{author}, written_date = #{written_date}, publisher = #{publisher}, publication_date = #{publication_date}, pages = #{pages}, language = #{language}, copies = #{copies} WHERE id = #{id}")
    boolean update(Book book);

    @Update("UPDATE books SET deleted_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void delete(long id);

    @Select("SELECT * FROM books WHERE " +
            "IF(#{title} IS NOT NULL, title = #{title}, title IS NOT NULL) AND " +
            "IF(#{author} IS NOT NULL, author = #{author}, author IS NOT NULL) AND " +
            "IF(#{written_date} IS NOT NULL, written_date = #{written_date}, written_date IS NOT NULL) AND " +
            "IF(#{publisher} IS NOT NULL, publisher = #{publisher}, publisher IS NOT NULL) AND " +
            "IF(#{publication_date} IS NOT NULL, publication_date = #{publication_date}, publication_date IS NOT NULL) AND " +
            "IF(#{pages} IS NOT NULL, pages = #{pages}, title IS NOT NULL) AND " +
            "IF(#{language} IS NOT NULL, language = #{language}, language IS NOT NULL) AND " +
            "deleted_at IS NULL")
    List<Book> search(Book book);
}

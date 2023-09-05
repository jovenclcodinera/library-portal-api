package com.joven.libraryportalapi.repositories;

import com.joven.libraryportalapi.models.Genre;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GenresRepository {

    @Insert("INSERT INTO genres (name) VALUES (#{name})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long save(Genre genre);

    @Select("SELECT last_insert_id()")
    long lastInsertId();

    @Select("SELECT * FROM genres WHERE name = #{name}")
    Genre findByName(String name);

    @Select("SELECT * FROM genres WHERE id = #{id}")
    Genre findById(Long id);
}

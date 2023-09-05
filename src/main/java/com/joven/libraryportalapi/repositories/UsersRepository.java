package com.joven.libraryportalapi.repositories;

import com.joven.libraryportalapi.models.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UsersRepository {

    @Select("SELECT * FROM users WHERE email = #{email} AND password = #{password}")
    User findByEmailAndPassword(User user);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Insert("INSERT INTO users (username, email, password, role) VALUES(#{username}, #{email}, #{password}, #{role})")
    void save(User user);

    @Select("SELECT last_insert_id()")
    long lastInsertId();

    @Select("SELECT * FROM users WHERE id = ${id}")
    User findById(Long id);
}

package com.joven.libraryportalapi.services;

import com.joven.libraryportalapi.exceptions.ResourceAlreadyExistsException;
import com.joven.libraryportalapi.exceptions.ResourceNotFoundException;
import com.joven.libraryportalapi.models.User;
import com.joven.libraryportalapi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = usersRepository.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException(String.format("User with email: %s does not exists", email));

        return user;
    }

    public User findByEmail(String email) {
        User user = usersRepository.findByEmail(email.trim());
        if (user == null) {
            throw new ResourceNotFoundException("User", "Email", email);
        }

        return user;
    }

    public User save(User user) {
        User fetchedUser = usersRepository.findByEmail(user.getEmail().trim());
        if (fetchedUser != null) {
            throw new ResourceAlreadyExistsException("User", "Email", fetchedUser.getEmail());
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        usersRepository.save(user);
        Long savedId = usersRepository.lastInsertId();
        return usersRepository.findById(savedId);
    }
}

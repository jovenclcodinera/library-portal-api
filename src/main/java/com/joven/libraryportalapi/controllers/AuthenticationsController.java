package com.joven.libraryportalapi.controllers;

import com.joven.libraryportalapi.models.AuthenticationRequest;
import com.joven.libraryportalapi.models.AuthenticationResponse;
import com.joven.libraryportalapi.models.User;
import com.joven.libraryportalapi.services.UserDetailsService;
import com.joven.libraryportalapi.utilities.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthenticationsController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtility jwtUtility;

    @PostMapping("login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        User user = (User) userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        String jwt = jwtUtility.generateToken(user);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User newUser = userDetailsService.save(user);
        Map<String, Object> map = new HashMap<>();
        map.put("message", String.format("User: %s has been created", newUser.getUsername()));

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", newUser.getId());
        userMap.put("username", newUser.getUsername());
        userMap.put("role", newUser.getRole());
        userMap.put("email", newUser.getEmail());
        userMap.put("isActive", newUser.isActive());

        map.put("data", userMap);

        return new ResponseEntity<Object>(map, HttpStatus.CREATED);
    }
}

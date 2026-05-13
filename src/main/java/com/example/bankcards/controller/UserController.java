package com.example.bankcards.controller;

import com.example.bankcards.exception.UserEmailNotUniqueException;
import com.example.bankcards.exception.UserNameNotUniqueException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.model.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDto saveUser(@RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
    }

    @ExceptionHandler(UserNameNotUniqueException.class)
    public ResponseEntity<String> handleNameNotUnique() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с таким именем уже существует");
    }

    @ExceptionHandler(UserEmailNotUniqueException.class)
    public ResponseEntity<String> handleEmailNotUnique() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с такой почтой уже существует");
    }
}

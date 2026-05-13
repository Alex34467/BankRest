package com.example.bankcards.service;

import com.example.bankcards.converter.UserConverter;
import com.example.bankcards.dto.UserDetailsDto;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.CardNumberNotUniqueException;
import com.example.bankcards.exception.UserEmailNotUniqueException;
import com.example.bankcards.exception.UserNameNotUniqueException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.model.UserDto;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return UserConverter.toDtos(userRepository.findAll());
    }

    public UserDto getById(Long id) {
        try {
            return UserConverter.toDto(userRepository.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(e);
        }
    }

    public UserDetailsDto getByUsername(String username) {
        Optional<UserEntity> entityOptional = userRepository.findByUsername(username);
        if (entityOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        return UserConverter.toUserDetailsDto(entityOptional.get());
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public UserDto saveUser(UserDto userDto) {
        validateUser(userDto);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserEntity entity = UserConverter.toEntity(userDto);
        userRepository.save(entity);
        return UserConverter.toDto(entity);
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException();
        }
    }

    public UserDetailsService getUserDetailsService() {
        return this::getByUsername;
    }

    private void validateUser(UserDto user) {
        Optional<UserEntity> otherUserByUsername = userRepository.findByUsername(user.getUsername());
        if (otherUserByUsername.isPresent()) {
            if (!otherUserByUsername.get().getId().equals(user.getId())) {
                throw new UserNameNotUniqueException();
            }
        }
        Optional<UserEntity> otherUserByEmail = userRepository.findByEmail(user.getEmail());
        if (otherUserByEmail.isPresent()) {
            if (!otherUserByEmail.get().getId().equals(user.getId())) {
                throw new UserEmailNotUniqueException();
            }
        }
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}

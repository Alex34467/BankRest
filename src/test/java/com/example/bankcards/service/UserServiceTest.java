package com.example.bankcards.service;

import com.example.bankcards.dto.UserDetailsDto;
import com.example.bankcards.model.UserDto;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsersTest_success () {
        Mockito.when(userRepository.findAll())
                .thenReturn(TestUtil.getUserEntities());

        List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getByIdTest_success () {
        Mockito.when(userRepository.getReferenceById(anyLong()))
                .thenReturn(TestUtil.getUserEntity());

        UserDto user = userService.getById(1L);

        assertNotNull(user);
        Mockito.verify(userRepository, Mockito.times(1)).getReferenceById(anyLong());
    }

    @Test
    void getByUsernameTest_success () {
        Mockito.when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(TestUtil.getUserEntity()));

        UserDetailsDto user = userService.getByUsername("username");

        assertNotNull(user);
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(anyString());
    }

    @Test
    void existsByIdTest_success () {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        boolean result = userService.existsById(1L);

        assertTrue(result);
        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
    }

    @Test
    void saveUserTest_success () {
        Mockito.when(passwordEncoder.encode(anyString()))
                        .thenReturn("123456");
        Mockito.when(userRepository.save(any()))
                .thenReturn(TestUtil.getUserEntity());

        UserDto saved = userService.saveUser(TestUtil.getUserDto());

        assertNotNull(saved);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(any());
    }

    @Test
    void deleteUserTest_success () {
        Mockito.when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(anyLong());
    }

}
package com.example.bankcards.controller;

import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getAllUsersTest_success() throws Exception {
        Mockito.when(userService.getAllUsers())
                        .thenReturn(TestUtil.getUsersDtos());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").exists())
                .andExpect(jsonPath("$.[0].id").exists());
        Mockito.verify(userService, Mockito.times(1)).getAllUsers();
    }

    @Test
    void getUserByIdTest_success() throws Exception {
        Mockito.when(userService.getById(anyLong()))
                .thenReturn(TestUtil.getUserDto());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        Mockito.verify(userService, Mockito.times(1)).getById(anyLong());
    }

    @Test
    void saveUserTest_success() throws Exception {
        Mockito.when(userService.saveUser(any()))
                .thenReturn(TestUtil.getUserDto());
        String json = new ObjectMapper().writeValueAsString(TestUtil.getUserDto());

        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        Mockito.verify(userService, Mockito.times(1)).saveUser(any());
    }

    @Test
    void deleteUserTest_success() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).deleteUser(anyLong());
    }
}

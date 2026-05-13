package com.example.bankcards.controller;

import com.example.bankcards.model.AuthRequest;
import com.example.bankcards.model.AuthResponse;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    void signInTest_success() throws Exception {
        Mockito.when(authenticationService.signIn(any()))
                .thenReturn(new AuthResponse("123456"));

        AuthRequest request = new AuthRequest("username", "123456");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        mockMvc.perform(
                post("/auth/sign-in")
                        .contentType("application/json")
                        .content(json)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}

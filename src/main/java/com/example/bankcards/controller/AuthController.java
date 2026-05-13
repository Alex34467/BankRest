package com.example.bankcards.controller;

import com.example.bankcards.model.AuthRequest;
import com.example.bankcards.model.AuthResponse;
import com.example.bankcards.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/sign-in")
    public AuthResponse signIn(@RequestBody @Valid AuthRequest request) {
        return authenticationService.signIn(request);
    }
}
package com.backbase.security.controller;

import com.backbase.security.JwtTokenProvider;
import com.backbase.security.dto.*;
import com.backbase.security.entity.Client;
import com.backbase.security.service.ClientService;
import com.backbase.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Client and user registration, and token generation")
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final ClientService clientService;

    public AuthController(JwtTokenProvider tokenProvider, UserService userService, ClientService clientService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.clientService = clientService;
    }

    @Operation(summary = "Generate a JWT access token")
    @PostMapping("/token")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        boolean isValidClient = clientService.isValid(request.clientId(), request.clientSecret());
        boolean isValidUser = userService.isValid(request.username(), request.password());

        if (!isValidClient) {
            throw new SecurityException("Client isn't valid");
        }

        if (!isValidUser) {
            throw new SecurityException("User doesn't exist or user name and password  isn't correct");
        }

        String token = tokenProvider.createToken(request.clientId(), request.clientSecret(), request.username());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/users")
    public ResponseEntity<UserRegistrationResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        userService.register(request.username(), request.password());
        return ResponseEntity.ok(new UserRegistrationResponse("User registered successfully.", request.username()));
    }

    @Operation(summary = "Register a new client and receive credentials")
    @PostMapping("/clients")
    public ResponseEntity<ClientRegistrationResponse> registerClient() {
        Client client = clientService.registerClient();
        return ResponseEntity.ok(new ClientRegistrationResponse(client.getClientId(), client.getClientSecret()));
    }
}

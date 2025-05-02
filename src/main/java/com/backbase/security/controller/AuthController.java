package com.backbase.security.controller;

import com.backbase.security.JwtTokenProvider;
import com.backbase.security.dto.auth.AuthRequest;
import com.backbase.security.dto.auth.AuthResponse;
import com.backbase.security.dto.auth.ClientRegistrationResponse;
import com.backbase.security.dto.auth.UserRegistrationRequest;
import com.backbase.security.entity.Client;
import com.backbase.security.service.ClientService;
import com.backbase.security.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final ClientService clientService;

    public AuthController(JwtTokenProvider tokenProvider, UserService userService, ClientService clientService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.clientService = clientService;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request, @RequestParam String username, @RequestParam String password) {
        boolean isValidClient = clientService.isValid(request.clientId(), request.clientSecret());
        boolean isValidUser = userService.isValid(username, password);

        if (!isValidClient) {
            throw new SecurityException("Client isn't valid");
        }

        if (!isValidUser) {
            throw new SecurityException("User doesn't exist or user name and password  isn't correct");
        }

        String token = tokenProvider.createToken(request.clientId(), username);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/users")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        userService.register(request.username(), request.password());
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientRegistrationResponse> registerClient() {
        Client credentials = clientService.registerClient();
        return ResponseEntity.ok(new ClientRegistrationResponse(credentials.getClientId(), credentials.getHashedSecret()));
    }
}

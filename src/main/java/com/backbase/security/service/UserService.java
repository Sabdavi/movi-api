package com.backbase.security.service;

import com.backbase.security.entity.User;
import com.backbase.security.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean exists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void register(String username, String rawPassword) {
        if (exists(username)) {
            throw new IllegalArgumentException("Username already taken.");
        }
        String hashedPassword = encoder.encode(rawPassword);
        userRepository.save(User.builder().username(username).hashedPassword(hashedPassword).build());
    }

    public boolean isValid(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> encoder.matches(rawPassword, user.getHashedPassword()))
                .orElse(false);
    }
}

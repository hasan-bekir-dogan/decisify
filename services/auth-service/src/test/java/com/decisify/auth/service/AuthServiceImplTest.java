package com.decisify.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceImplTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldHashPasswordWithBCrypt() {
        String rawPassword = "Password123!";

        String hashedPassword = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword));
    }
}
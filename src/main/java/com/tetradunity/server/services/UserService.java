package com.tetradunity.server.services;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:'\"\\,.<>?/";

    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}

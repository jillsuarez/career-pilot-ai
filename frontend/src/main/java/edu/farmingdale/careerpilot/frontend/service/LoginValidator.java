package edu.farmingdale.careerpilot.frontend.service;

import java.util.regex.Pattern;

public class LoginValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    public String validate(String email, String password) {
        if (email == null || email.isBlank()) {
            return "Email is required.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Enter a valid email address.";
        }
        if (password == null || password.isBlank()) {
            return "Password is required.";
        }
        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            return "Password must contain at least 6 characters.";
        }
        return null;
    }
}

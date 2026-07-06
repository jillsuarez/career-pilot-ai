package edu.farmingdale.careerpilot.frontend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LoginValidatorTests {

    private final LoginValidator validator = new LoginValidator();

    @Test
    void acceptsValidCredentials() {
        assertNull(validator.validate("student@example.com", "secret1"));
    }

    @Test
    void requiresEmail() {
        assertEquals("Email is required.", validator.validate(" ", "secret1"));
    }

    @Test
    void rejectsMalformedEmail() {
        assertEquals("Enter a valid email address.", validator.validate("student", "secret1"));
    }

    @Test
    void requiresPassword() {
        assertEquals("Password is required.", validator.validate("student@example.com", ""));
    }

    @Test
    void rejectsShortPassword() {
        assertEquals(
                "Password must contain at least 6 characters.",
                validator.validate("student@example.com", "12345")
        );
    }
}

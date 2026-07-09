package edu.farmingdale.careerpilot.frontend.service;

/**
 * Temporary local authentication used until the backend exposes an authentication API.
 */
public class DemoAuthenticationService implements AuthenticationService {

    @Override
    public boolean authenticate(String email, String password) {
        return email != null && !email.isBlank() && password != null && !password.isBlank();
    }
}

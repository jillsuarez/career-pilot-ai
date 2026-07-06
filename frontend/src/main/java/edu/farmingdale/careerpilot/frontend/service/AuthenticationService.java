package edu.farmingdale.careerpilot.frontend.service;

public interface AuthenticationService {

    boolean authenticate(String email, String password);
}

package com.project.back_end.DTO;

public class Login {
    
    // 1. 'identifier' field:
    private String identifier;
    
    // 2. 'password' field:
    private String password;

    // 3. Constructors:
    // Default constructor
    public Login() {
    }

    // Parameterized constructor for convenience
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // 4. Getters and Setters:
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
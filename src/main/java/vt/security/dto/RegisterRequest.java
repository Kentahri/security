package vt.security.dto;

public record RegisterRequest(
        String username,
        String password
) {}
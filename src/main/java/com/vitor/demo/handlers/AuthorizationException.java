package com.vitor.demo.handlers;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
    
    public AuthorizationException() {
        super("Acesso negado!");
    }
}
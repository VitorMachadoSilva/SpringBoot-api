package com.vitor.demo.handlers;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
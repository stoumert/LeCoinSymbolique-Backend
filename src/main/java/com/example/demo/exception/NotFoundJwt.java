package com.example.demo.exception;

public class NotFoundJwt extends RuntimeException {
    public NotFoundJwt(String message) {
        super(message);
    }
}

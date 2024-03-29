package com.example.exceptions;

public class JwtParsingException extends RuntimeException{
    public JwtParsingException(String message) {
        super(message);
    }
}

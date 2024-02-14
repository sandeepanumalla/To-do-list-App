package com.example.exceptions;

public class UsernameOrEmailAlreadyTakenException extends RuntimeException{
    public UsernameOrEmailAlreadyTakenException(String message) {
        super(message);
    }
}

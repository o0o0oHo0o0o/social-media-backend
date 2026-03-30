package com.example.SocialMedia.exception;

public class TooManyFileException extends RuntimeException {
    public TooManyFileException(String message) {
        super(message);
    }
}

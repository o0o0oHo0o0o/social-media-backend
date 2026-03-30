package com.example.SocialMedia.exception;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(String message) {
        super(message); // Pass the error message to the parent class (RuntimeException)
    }
}

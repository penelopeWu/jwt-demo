package com.example.jwtdemo.exception;

public class KCException extends RuntimeException{

    private int status;
    private String message;

    public KCException() {
        super();
    }

    public KCException(String message, int status) {
        this.message = message;
        this.status = status;
    }
}

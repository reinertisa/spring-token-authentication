package com.reinertisa.sta.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    public ApiException() {
        super("An unknown error occurred");
    }

}

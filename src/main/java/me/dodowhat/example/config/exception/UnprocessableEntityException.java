package me.dodowhat.example.config.exception;

public class UnprocessableEntityException extends Exception {
    public UnprocessableEntityException() {
        super();
    }
    public UnprocessableEntityException(String message) {
        super(message);
    }
}

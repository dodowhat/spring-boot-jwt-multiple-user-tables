package me.dodowhat.example.config.exception;

public class ActionNotAllowedException extends Exception {
    public ActionNotAllowedException(String message) {
        super(message);
    }
}

package dev.testment.core.cli.exceptions;

public class MissingValueException extends RuntimeException {
    public MissingValueException(String message) {
        super(message);
    }
}

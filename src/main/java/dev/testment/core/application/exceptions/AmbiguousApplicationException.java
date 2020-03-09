package dev.testment.core.application.exceptions;

public class AmbiguousApplicationException extends RuntimeException {
    public AmbiguousApplicationException(String message) {
        super(message);
    }
}

package dev.testment.core.util.exceptions;

public class HttpStatusCodeException extends RuntimeException {
    public HttpStatusCodeException(String message) {
        super(message);
    }
}

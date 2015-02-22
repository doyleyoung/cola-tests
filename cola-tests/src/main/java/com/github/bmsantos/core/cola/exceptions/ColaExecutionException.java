package com.github.bmsantos.core.cola.exceptions;

public class ColaExecutionException extends RuntimeException {

    private static final long serialVersionUID = 993649109011442203L;

    public ColaExecutionException() {
        super();
    }

    public ColaExecutionException(final String message) {
        super(message);
    }

    public ColaExecutionException(final Throwable cause) {
        super(cause);
    }

    public ColaExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

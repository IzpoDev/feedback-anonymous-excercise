//Esta excepcion devuelve un 409, para recursos duplicados
package com.feedback.feedback.common.exception;

public class DuplicateResouceException extends RuntimeException {
    public DuplicateResouceException(String message) {
        super(message);
    }
}

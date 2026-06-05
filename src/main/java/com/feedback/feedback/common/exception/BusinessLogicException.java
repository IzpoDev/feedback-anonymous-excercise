//Esta esception debe devolver un 400, para errores de reglas de negocio
package com.feedback.feedback.common.exception;

public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException(String message) {
        super(message);
    }
}

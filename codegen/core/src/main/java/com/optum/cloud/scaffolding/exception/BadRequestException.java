package com.optum.cloud.scaffolding.exception;

public class BadRequestException extends CodegenException {
    // Exception used for errors caused by bad input data to codegen endpoints
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Exception e) {
        super(message, e);
    }
}

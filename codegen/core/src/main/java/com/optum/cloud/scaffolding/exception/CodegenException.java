package com.optum.cloud.scaffolding.exception;

public class CodegenException extends Exception {
    public CodegenException(String message) {
        super(message);
    }

    public CodegenException(String message, Exception e) {
        super(message);
    }
}

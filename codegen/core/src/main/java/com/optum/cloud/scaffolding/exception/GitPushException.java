package com.optum.cloud.scaffolding.exception;

public class GitPushException extends CodegenException {
    // Exception used for errors that occur when writing to the git repo in the codegen endpoint
    public GitPushException(String message) {
        super(message);
    }

    public GitPushException(String message, Exception e) {
        super(message, e);
    }
}

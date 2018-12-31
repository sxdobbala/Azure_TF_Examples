package com.optum.acc.fileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureContainerException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureContainerException.class);

    public AzureContainerException(String message) {
        super(message);
    }

    public AzureContainerException(Throwable cause) {
        super(cause.getMessage());
        LOGGER.info(cause.getMessage(), cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        // don't keep a stacktrace
    }
}

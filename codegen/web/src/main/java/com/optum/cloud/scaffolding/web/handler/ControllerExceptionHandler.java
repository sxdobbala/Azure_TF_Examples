package com.optum.cloud.scaffolding.web.handler;

import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.GitPushException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ControllerExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    public static <T> ResponseEntity<T> handleException(Exception e, T body) {
        final HttpStatus status;
        final HttpHeaders headers = new HttpHeaders();
        final String error;
        final String errorMessage;

        if (e.getClass() == BadRequestException.class) {
            status = HttpStatus.BAD_REQUEST;
            error = "Bad Request";
            errorMessage = "Invalid request: " + e.getMessage();
        } else if (e.getClass() == GitPushException.class) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
            errorMessage = "Error communicating with GitHub: " + e.getMessage();
        } else if (e.getClass() == IOException.class) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
            errorMessage = "IO Exception occurred during file transfer: " + e.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Internal Server Error";
            errorMessage = "Error in service: " + e.getMessage();
        }

        headers.add("exception", e.getClass().toString());
        headers.add("error", error);
        headers.add("message", errorMessage);

        logger.error(error + " - " + errorMessage);

        return new ResponseEntity<T>(body, headers, status);
    }
}

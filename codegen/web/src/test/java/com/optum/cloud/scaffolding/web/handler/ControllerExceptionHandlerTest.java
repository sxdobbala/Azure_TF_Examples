package com.optum.cloud.scaffolding.web.handler;

import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.GitPushException;
import com.optum.cloud.scaffolding.web.handler.ControllerExceptionHandler;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ControllerExceptionHandlerTest {

    @Test
    public void testBadRequestException() {
        ResponseEntity<String> entity = ControllerExceptionHandler.handleException(new BadRequestException("mess"), "hey");
        assertNotNull(entity);
        assertEquals(entity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGitPushException() {
        ResponseEntity<String> entity = ControllerExceptionHandler.handleException(new GitPushException("mess"), "hey");
        assertNotNull(entity);
        assertEquals(entity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testDefaultException() {
        ResponseEntity<String> entity = ControllerExceptionHandler.handleException(new IOException("no"), "hey");
        assertNotNull(entity);
        assertEquals(entity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

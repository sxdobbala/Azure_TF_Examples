package com.optum.cloud.scaffolding.web.controllers;

import com.optum.cloud.scaffolding.service.CodeGenService;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.web.handler.ControllerExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class CodeGenController {

    private static Logger logger = LoggerFactory.getLogger(CodeGenController.class);

    /**
     * Allows the caller to generate the code to back an application manifest.  The resulting
     * code will be checked in to the code hub repository associated with the manifest.
     *
     * @param manifest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "api/v1/codegen/generate")
    public ResponseEntity<Manifest> generate(@Valid @RequestBody Manifest manifest) {

        ResponseEntity<Manifest> entity;
        Manifest response;

        logger.info("\n------------------------------");
        logger.info("CodeGen-Controller");
        logger.info("Request received: " + manifest);

        try {
            response = CodeGenService.generateCodeForManifest(manifest);
            entity = new ResponseEntity<Manifest>(response, HttpStatus.OK);
        } catch (CodegenException | IOException e) {
            response = manifest;
            entity = ControllerExceptionHandler.handleException(e, response);
        }

        logger.info("Response: " + response);
        logger.info("\n------------------------------");

        return entity;
    }
}

package com.optum.cloud.scaffolding.web.controllers;


import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.service.ManifestService;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import com.optum.cloud.scaffolding.web.handler.ControllerExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;


@RestController
public class ManifestController {

    private static Logger logger = LoggerFactory.getLogger(ManifestController.class);

    /**
     * Generate an application manifest with default features for the application type/subtype.
     *
     * @param manifest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, path = "api/v1/manifest/generate")
    public ResponseEntity<Manifest> generate(@Valid @RequestBody Manifest manifest) {

        ResponseEntity<Manifest> entity;
        Manifest response;

        logger.info("\n------------------------------");
        logger.info("Manifest-Controller");
        logger.info("Request received: " + manifest);

        try {
            response = ManifestService.generateManifestFrom(manifest);
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

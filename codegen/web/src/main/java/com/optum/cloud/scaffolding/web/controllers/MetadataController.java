package com.optum.cloud.scaffolding.web.controllers;


import com.optum.cloud.scaffolding.service.MetadataService;
import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.web.handler.ControllerExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MetadataController {

    private static Logger logger = LoggerFactory.getLogger(MetadataController.class);

    @RequestMapping(method = RequestMethod.GET, path = "api/v1/metadata/applications/types")
    public List<String> getAllApplicationTypes() {
        logger.info("\n------------------------------");
        logger.info("Metadata-Controller");
        logger.info("Application Types");
        List<String> supportedAppTypes;

        try {
            supportedAppTypes = MetadataService.getAvailableTypes();
            logger.info("Supported App Types: " + supportedAppTypes);
        } catch (IOException io) {
            supportedAppTypes = new ArrayList<>();
            logger.error("There was an IO exception while handling the request");
        }

        logger.info("\n------------------------------");

        return supportedAppTypes;
    }

    @RequestMapping(method = RequestMethod.GET, path = "api/v1/metadata/applications/{applicationType}/subtypes")
    public ResponseEntity<List<String>> getAllApplicationSubTypes(@PathVariable String applicationType) {
        logger.info("\n------------------------------");
        logger.info("Metadata-Controller");
        logger.info("Application Subtypes");
        logger.info("Request received: " + applicationType);

        ResponseEntity<List<String>> entity;
        List<String> supportedAppSubtypes;

        try {
            if (!MetadataService.getAvailableTypes().contains(applicationType)) {
                logger.info("Invalid App Type provided: " + applicationType);
                throw new BadRequestException("Invalid App Type " + applicationType);
            } else {
                supportedAppSubtypes = MetadataService.getSubtypesFor(applicationType);
                entity = new ResponseEntity<List<String>>(supportedAppSubtypes, HttpStatus.OK);
                logger.info("Supported App Subtypes for " + applicationType + ": " + supportedAppSubtypes);

            }
        } catch (BadRequestException | IOException e) {
            entity = ControllerExceptionHandler.handleException(e, new ArrayList<>());
        }

        logger.info("\n------------------------------");

        return entity;
    }

    @RequestMapping(method = RequestMethod.GET, path = "api/v1/metadata/applications/{applicationType}/{applicationSubtype}/{paas}/features")
    public ResponseEntity<List<String>> getFeaturesFor(@PathVariable String applicationType, @PathVariable String applicationSubtype, @PathVariable String paas) {
        ResponseEntity<List<String>> entity;
        ArrayList<String> featureList;

        try {
            if (MetadataService.getAvailableTypes().contains(applicationType)) {
                if (MetadataService.getSubtypesFor(applicationType).contains(applicationSubtype)) {
                    if (MetadataService.getPlatformsFor(applicationType, applicationSubtype).contains(paas)) {
                        featureList = MetadataService.getFeaturesFor(applicationType, applicationSubtype, paas);
                        logger.info(String.format("The list of features for a %s %s application on paas %s are %s", applicationType, applicationSubtype, paas, featureList));
                        entity = new ResponseEntity<>(featureList, HttpStatus.OK);
                    } else {
                        throw new BadRequestException("Invalid PaaS Platform for subtype " + applicationSubtype + " of app type " + applicationType);
                    }
                } else {
                    throw new BadRequestException("Invalid App Subtype " + applicationSubtype);
                }
            } else {
                throw new BadRequestException("Invalid App Type " + applicationType);
            }
        } catch (BadRequestException | IOException e) {
            entity = ControllerExceptionHandler.handleException(e, new ArrayList<>());
        }

        return entity;
    }
}

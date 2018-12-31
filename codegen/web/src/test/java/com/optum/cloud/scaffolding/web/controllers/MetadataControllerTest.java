package com.optum.cloud.scaffolding.web.controllers;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MetadataControllerTest {

    @Test
    public void applicationTypesCallSucceeds() {
        List<String> result = new MetadataController().getAllApplicationTypes();
        Assert.assertNotNull("Types are returned", result);
        Assert.assertTrue("More than one type exists:", result.size() > 0);
    }

    @Test
    public void applicationSubtypesCallSucceedsWithValidParams() {
        ResponseEntity result = new MetadataController().getAllApplicationSubTypes("peds");
        Assert.assertNotNull("Response is returned", result);
        Assert.assertEquals("Response code is 200 OK", HttpStatus.OK, result.getStatusCode());
        Assert.assertTrue("Response contains at least one element", ((List) result.getBody()).size() > 0);
    }

    @Test
    public void applicationSubtypesCallFailsWithInvalidParams() {
        ResponseEntity result = new MetadataController().getAllApplicationSubTypes("nope");
        Assert.assertNotNull("Response is returned", result);
        Assert.assertEquals("Response code is 400 bad Request", HttpStatus.BAD_REQUEST, result.getStatusCode());
        Assert.assertTrue("Response contains no elements", ((List) result.getBody()).size() == 0);
    }

    @Test
    public void getFeaturesCallSucceedsWithValidParams() {
        ResponseEntity result = new MetadataController().getFeaturesFor("peds", "microservice", "openshift");
        Assert.assertNotNull("Response is returned", result);
        Assert.assertEquals("Response code is 200 OK", HttpStatus.OK, result.getStatusCode());
        Assert.assertTrue("Response contains at least one element", ((List) result.getBody()).size() > 0);
    }

    @Test
    public void getFeaturesCallFailsWithInvalidParams() {
        ResponseEntity result = new MetadataController().getFeaturesFor("nope", "nope", "nope");
        Assert.assertNotNull("Response is returned", result);
        Assert.assertEquals("Response code is 400 Bad Request", HttpStatus.BAD_REQUEST, result.getStatusCode());
        Assert.assertTrue("Response contains no elements", ((List) result.getBody()).size() == 0);
    }
}

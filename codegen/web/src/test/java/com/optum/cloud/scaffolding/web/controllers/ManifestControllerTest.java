package com.optum.cloud.scaffolding.web.controllers;

import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.service.ManifestService;
import com.optum.cloud.scaffolding.web.testutils.Generator;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ManifestService.class)
public class ManifestControllerTest {

    private Manifest manifest;

    @Before
    public void setup() {
        PowerMockito.mockStatic(ManifestService.class);
        manifest = Generator.generateDefaultManifest("testapp", "testteam", "openshift");
    }

    @Test
    public void successfulResponseGivenUsingValidInformation() throws IOException, CodegenException {
        PowerMockito.when(ManifestService.generateManifestFrom(manifest)).thenReturn(manifest);
        ResponseEntity response = new ManifestController().generate(manifest);

        Assert.assertTrue("Response returns OK", response.getStatusCode() == HttpStatus.OK);
        Assert.assertTrue("Response returns manifest", response.getBody() == manifest);
    }

    @Test
    public void failureResponseGivenUsingBadInformation() throws Exception {
        PowerMockito.doThrow(new BadRequestException("test exception")).when(ManifestService.class, "generateManifestFrom", manifest);
        ResponseEntity response = new ManifestController().generate(manifest);

        Assert.assertTrue("Response returns Bad Request", response.getStatusCode() == HttpStatus.BAD_REQUEST);
        Assert.assertTrue("Response returns manifest", response.getBody() == manifest);
    }
}

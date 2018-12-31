package com.optum.cloud.scaffolding.web.controllers;

import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.service.CodeGenService;
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
@PrepareForTest(CodeGenService.class)
public class CodeGenControllerTest {


    @Before
    public void setup() {
        PowerMockito.mockStatic(CodeGenService.class);
    }

    @Test
    public void generateReturnsOkWhenCallingWithValidInformation() throws IOException, CodegenException {
        Manifest manifest = Generator.generateDefaultManifest("test", "testteam", "openshift");
        PowerMockito.when(CodeGenService.generateCodeForManifest(manifest)).thenReturn(manifest);
        ResponseEntity response = new CodeGenController().generate(manifest);

        Assert.assertTrue("Response returns OK", response.getStatusCode() == HttpStatus.OK);
        Assert.assertTrue("Response returns manifest", response.getBody() == manifest);
    }

    @Test
    public void generateReturnsErrorWhenCallingWithInvalidInformation() throws Exception {
        Manifest manifest = Generator.generateDefaultManifest("test", "testteam", "openshift");
        PowerMockito.doThrow(new BadRequestException("yay")).when(CodeGenService.class, "generateCodeForManifest", manifest);
        ResponseEntity response = new CodeGenController().generate(manifest);

        Assert.assertTrue("Response returns Bad Request", response.getStatusCode() == HttpStatus.BAD_REQUEST);
        Assert.assertTrue("Response returns manifest", response.getBody() == manifest);
    }
}

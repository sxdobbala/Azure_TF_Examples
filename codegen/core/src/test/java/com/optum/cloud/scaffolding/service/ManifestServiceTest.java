package com.optum.cloud.scaffolding.service;

import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.testutils.Generator;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import org.junit.Test;

import java.io.IOException;

public class ManifestServiceTest {

    private String paas = "openshift";

    @Test
    public void manifestGeneratesSuccessfullyGivenValidManifest() throws IOException, CodegenException {
        Manifest manifest = Generator.generateDefaultManifest("peds", "microservice", paas);

        ManifestService.generateManifestFrom(manifest);
    }

    @Test(expected = BadRequestException.class)
    public void manifestGenerationFailsWhenApplicationIsMissing() throws IOException, CodegenException {
        Manifest manifest = Generator.generateDefaultManifest("invalid", "invalid", paas);
        manifest.setApplication(null);

        ManifestService.generateManifestFrom(manifest);
    }

    @Test(expected = BadRequestException.class)
    public void manifestGenerationFailsWhenTypeIsInvalid() throws IOException, CodegenException {
        Manifest manifest = Generator.generateDefaultManifest("invalid", "invalid", paas);
        manifest.getApplication().setApplicationType("invalid");

        ManifestService.generateManifestFrom(manifest);
    }

    @Test(expected = BadRequestException.class)
    public void manifestGenerationFailsWhenSubtypeIsInvalid() throws IOException, CodegenException {
        Manifest manifest = Generator.generateDefaultManifest("invalid", "invalid", paas);
        manifest.getApplication().setSubType("invalid");

        ManifestService.generateManifestFrom(manifest);
    }

}

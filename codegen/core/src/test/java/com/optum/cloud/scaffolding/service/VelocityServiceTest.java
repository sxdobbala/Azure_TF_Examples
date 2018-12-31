package com.optum.cloud.scaffolding.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import com.optum.cloud.scaffolding.testutils.Generator;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import org.junit.Test;

public class VelocityServiceTest {

    @Test
    public void verifyGenerationHandlesInvalidCharacters() throws IOException {

        Path generatedRoot = generateCode("%unacceptable%", "%unacceptable2%", "openshift");

        Path aspectFile = generatedRoot.resolve("unacceptable-web/pom.xml");
        assertTrue("pom.xml should be created by PEDS init template at expected location.",
            aspectFile.toFile().exists());
    }

    @Test
    public void verifyGenerationOS() throws IOException {

        Path generatedRoot = generateCode("mytest", "myteam", "openshift");

        Path aspectFile = generatedRoot.resolve("mytest-web/pom.xml");
        assertTrue("pom.xml should be created by PEDS init template at expected location.",
            aspectFile.toFile().exists());
    }

    @Test
    public void verifyGenerationMesos() throws IOException {

        Path generatedRoot = generateCode("mytest", "myteam", "mesos-marathon");

        Path aspectFile = generatedRoot.resolve("mytest-web/pom.xml");
        assertTrue("pom.xml should be created by PEDS init template at expected location.",
            aspectFile.toFile().exists());
    }

    @Test
    public void verifyPedsLogAspect() throws IOException {

        Path generatedRoot = generateCode("mytest", "myteam", "openshift");

        Path aspectFile = generatedRoot.resolve("mytest-web/src/main/java/com/optum/myteam/mytest/web/aspects/LoggingAspect.java");
        assertTrue("LoggingAspect.java should be created by PEDS init template at expected location.",
            aspectFile.toFile().exists());
    }

    private static Path generateCode(String appName, String teamName, String paas) throws IOException {
        Manifest manifest = Generator.generateDefaultManifest(appName, teamName, paas);
        Path tempPath = Files.createDirectories(Files.createTempDirectory("verify-generation"));

        final String velocityTemplateLocation = String.format("/velocity/%s/init", manifest.getApplication().getApplicationType().toLowerCase());
        HashMap<String, String> includePaths = MetadataService.determineIncludes(new ArrayList<>(), paas);

        VelocityService.generate(velocityTemplateLocation, includePaths, tempPath, manifest);

        return tempPath;
    }
}

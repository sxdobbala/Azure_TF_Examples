package com.optum.cloud.scaffolding.service;

import com.optum.cloud.scaffolding.testutils.Generator;
import com.optum.pbi.devops.toolchain.service.model.codegen.*;
import com.optum.cloud.scaffolding.exception.CodegenException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.util.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


public class CodeGenServiceTest {

    private final String vcsUri = "https://github.optum.com/cloud-scaffolding/codegentestrepo";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public CodeGenServiceTest() {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken("codegen_nonprod", "P!napple", null);

        SecurityContextHolder.getContext().setAuthentication(authToken);
        ;
    }

    @Test
    public void successfulProjectGenerationDespiteBadInput() throws IOException {

        Manifest manifest = Generator.generateDefaultManifest("%unacceptable%", "%unacceptable%", "openshift");

        final Path gitCloneFolder = Files.createTempDirectory("cloud-scaffolding-codegen-");

        File rootDirectory = null;
        try {
            rootDirectory = CodeGenService.generateProjectCodeBase(manifest, gitCloneFolder);
        } catch (CodegenException e) {
            fail("failed to run generateProjectCodeBase");
        }

        assertNotNull(rootDirectory);
        assertTrue(rootDirectory.isDirectory());
        assertTrue(rootDirectory.exists());

        Path rootPath = rootDirectory.toPath();

        Path webPomFilePath = rootPath.resolve("unacceptable-web/pom.xml");

        assertTrue("pom.xml should be created", webPomFilePath.toFile().exists());
        FileUtils.delete(rootDirectory, FileUtils.RECURSIVE);
    }

    @Test
    public void successfulProjectGenerationWithValidNameAndTeam() throws IOException {

        String projectName = "project";

        final Path gitCloneFolder = Files.createTempDirectory("cloud-scaffolding-codegen-");

        Manifest manifest = Generator.generateDefaultManifest(projectName, "projectteam", "openshift");

        File tempFile = null;

        try {
            tempFile = CodeGenService.generateProjectCodeBase(manifest, gitCloneFolder);
        } catch (CodegenException e) {
            fail("failed to run generateProjectCodeBase");
        }
        Path path = tempFile.toPath();

        Path pomFile = path.resolve(projectName + "-web/pom.xml");
        assertTrue("pom.xml should be created by PEDS init template at expected location.",
            pomFile.toFile().exists());

        FileUtils.delete(tempFile, FileUtils.RECURSIVE);
    }

}

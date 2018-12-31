package com.optum.cloud.scaffolding.integrationtest;

import com.optum.pbi.devops.toolchain.service.model.codegen.Application;
import com.optum.pbi.devops.toolchain.service.model.codegen.BillingInformation;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.Feature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.GitHubFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.JenkinsFeature;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CodeGenIntTest extends BaseAdaptor {
    private Map<String, String> headers;

    private Logger logger = LoggerFactory.getLogger(CodeGenIntTest.class);
    private String githubUri = "https://github.optum.com/cloud-scaffolding/codegentestrepo";
    private static String authString;
    private static String gitUser;
    private static String gitPass;

    @BeforeClass
    public static void setup() throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = CodeGenIntTest.class.getResourceAsStream("/application-secrets.yml");

        properties.load(inputStream);
        String pbiuser = properties.getProperty("com.optum.pbi.real.username");
        String password = properties.getProperty("com.optum.pbi.real.password");
        gitUser = properties.getProperty("com.optum.pbi.repo.username");
        gitPass = properties.getProperty("com.optum.pbi.repo.password");

        authString = pbiuser + ":" + password;
        inputStream.close();
    }

    @Before
    public void beforeTest() throws UnsupportedEncodingException {
        headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(authString.getBytes("utf-8")));
    }

    @Test
    public void codeGenerationSuccessfulWithValidInput() throws GitAPIException, IOException {
        BillingInformation billInfo = new BillingInformation("TMDB-0000000", "UHGWM110-000000");
        Application testApp = new Application("projectName", "projectteam", "projectOwner", "python", "microservice");
        ArrayList<Feature> features = new ArrayList<>();
        features.add(new GitHubFeature("cloud-scaffolding", "codegentestrepo", githubUri, "v1"));
        features.add(new JenkinsFeature("testJenkinsGroup", "test", "test", "test", false, "test", "v1", new ArrayList<>()));
        Manifest manifest = new Manifest("v1", "openshift", testApp, billInfo, features);
        CloseableHttpResponse response = postWithHeadersReturnHttpResponse("/api/v1/codegen/generate", manifest, headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns OK", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);

        Path codegenPath = Files.createTempDirectory("codegen-");

        Git git = Git.cloneRepository().setURI(githubUri)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUser, gitPass))
            .setDirectory(codegenPath.toFile()).call();
        git.close();

        checkFileExists(codegenPath, "Dockerfile");
        checkFileExists(codegenPath, "Jenkinsfile");
        checkFileExists(codegenPath, "Optumfile.yml");
        checkFileExists(codegenPath, "README.md");
        checkFileExists(codegenPath, "requirements.txt");
        checkFileExists(codegenPath, "wsgi.py");
    }

    @Test
    public void manifestGenerationFailsProperlyDueToInvalidInput() {
        Manifest manifest = new Manifest();
        CloseableHttpResponse response = postWithHeadersReturnHttpResponse("/api/v1/codegen/generate", manifest, headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns Bad Request", response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @After
    public void cleanUp() throws IOException, GitAPIException {
        logger.debug("cleanUp - start");

        Path codegenPath = Files.createTempDirectory("codegen-");

        logger.debug("cleanUp - cloning git repository");
        Git git = Git.cloneRepository().setURI(githubUri)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider("codegen_nonprod", "P!napple"))
            .setDirectory(codegenPath.toFile()).call();

        logger.debug("cleanUp - deleting files from repository");
        Files.walkFileTree(codegenPath, new DeleteFilesVisitor(codegenPath, git));

        git.commit().setAll(true).setMessage("cleanup").call();
        git.push().setRemote(githubUri)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider("codegen_nonprod", "P!napple")).call();
        git.close();

        logger.debug("cleanUp - deleting temp folder");
        FileUtils.delete(codegenPath.toFile(), FileUtils.RECURSIVE);
        logger.debug("cleanUp - complete");
    }

    public String readFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public void checkFileExists(Path path, String file) {
        Path filePath = path.resolve(file);
        assertTrue(file + " exists", filePath.toFile().exists());
    }

    private class DeleteFilesVisitor extends SimpleFileVisitor<Path> {
        private Path codegenPath;
        private Git git;

        public DeleteFilesVisitor(Path codegenPath, Git git) {
            this.codegenPath = codegenPath;
            this.git = git;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {

            try {
                git.rm().addFilepattern(path.toFile().getPath()).call();
            } catch (GitAPIException e) {
                logger.error(e.getMessage());
            }
            Files.delete(path);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
            if (path.getFileName().toString().equals(codegenPath.getFileName().toString())) {
                return FileVisitResult.TERMINATE;
            }
            try {
                git.rm().addFilepattern(path.toFile().getPath()).call();
            } catch (GitAPIException e1) {
                logger.error(e1.getMessage());
            }
            Files.delete(path);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

            if (dir.getFileName().toString().contains(".git")) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }
    }
}

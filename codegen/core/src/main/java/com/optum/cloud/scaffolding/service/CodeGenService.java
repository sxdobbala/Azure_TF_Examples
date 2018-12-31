package com.optum.cloud.scaffolding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.Feature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.JenkinsFeature;
import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.exception.GitPushException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeGenService {

    public static Manifest generateCodeForManifest(Manifest manifest) throws IOException, CodegenException {

        try {
            if (null == manifest) {
                throw new BadRequestException("manifest is null");
            }

            if (null == manifest.getApplication()) {
                throw new BadRequestException("must provide application");
            }

            if (null == manifest.getFeatures()) {
                throw new BadRequestException("must provide feature list");
            }

            if (checkDuplicateFeatures(manifest.getFeatures())) {
                throw new BadRequestException("duplicate features exist in manifest");
            }

            // Create temporary directories to hold files
            final Path gitCloneFolder = Files.createTempDirectory("cloud-scaffolding-codegen-");

            // Clone Git repo before code generation in case any files in repo created by
            // other processes previously in the choreography.
            cloneGitRepo(gitCloneFolder.toFile(), manifest);

            // Generate code base
            final File file = generateProjectCodeBase(manifest, gitCloneFolder);

            pushToGitRepo(manifest, file);

            // Clean-up
            FileUtils.deleteDirectory(file);
        } catch (IOException | CodegenException e) {
            throw e;
        }

        // Might need to update the manifest in service object ??
        return manifest;
    }

    private static boolean checkDuplicateFeatures(final List<Feature> featureList) {
        boolean duplicateExists = false;
        for (Feature feature : featureList) {
            final int duplicates = Collections.frequency(featureList, feature);
            if (duplicates > 1) {
                duplicateExists = true;
                break;
            }
        }
        return duplicateExists;
    }


    static File generateProjectCodeBase(final Manifest manifest, final Path gitCloneFolder) throws IOException, CodegenException {

        List<String> requestedTools = new ArrayList<>();
        for (Feature feature : manifest.getFeatures()) {
            if (feature.getName().toLowerCase().equals("jenkins")) {
                JenkinsFeature jenkins = (JenkinsFeature) feature;
                if (jenkins.getTools() != null) {
                    if (jenkins.getTools().size() > 0) {
                        requestedTools = jenkins.getTools();
                    }
                }
            }
        }

        String deployMethod;
        if (manifest.getPaas() != null && MetadataService.getPlatformsFor(manifest.getApplication().getApplicationType(), manifest.getApplication().getSubType()).contains(manifest.getPaas())) {
            deployMethod = manifest.getPaas() != null ? manifest.getPaas() : "";
        } else {
            throw new BadRequestException("PaaS parameter is not specified or it is not correct");
        }

        HashMap<String, String> includePaths = MetadataService.determineIncludes(requestedTools, deployMethod);

        final String velocityTemplates = String.format("/velocity/%s/init", manifest.getApplication().getApplicationType().toLowerCase());

        VelocityService.generate(velocityTemplates, includePaths, gitCloneFolder, manifest);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(gitCloneFolder + "/manifest.json"), manifest);
        return gitCloneFolder.toFile();
    }

    // The codehub feature and its URI must be present in the manifest.
    // Note that the '.git' extension at the end of the URI or the push fails due to a HTTP 302 redirect.
    // This is some issue with Codehub and JGit (and Eclipse).
    // Also user should not be in the URI (<MS ID>@ near the start).
    private static void pushToGitRepo(final Manifest manifest, final File codeDir) throws CodegenException {

        // this is to support codegen push to either github or codehub
        Feature gitFeature = manifest.getFeatureByName("github");

        if (gitFeature == null) {
            gitFeature = manifest.getFeatureByName("codehub");
        }

        if (gitFeature == null) {
            throw new BadRequestException("no github or codehub feature in manifest");
        }

        final String vcsUri = gitFeature.getUri();

        if (vcsUri == null || vcsUri.isEmpty()) {
            throw new BadRequestException("no github or codehub feature URI in manifest");
        }

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            throw new BadRequestException("user not authenticated");
        }

        try (Git git = Git.init().setDirectory(codeDir).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("CodeGen initial project.").call();

            final Iterable<PushResult> results = git.push().setRemote(vcsUri)
                .setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(auth.getName(), (String) auth.getCredentials()))
                .call();

            final PushResult pushResult = results.iterator().next();
            final RemoteRefUpdate.Status status = pushResult.getRemoteUpdate("refs/heads/master").getStatus();

            if (status != RemoteRefUpdate.Status.OK) {
                throw new GitPushException("pushToGitRepo: push to remote status not OK. Status = " + status);
            }
        } catch (GitAPIException e) {
            throw new GitPushException("pushToGitRepo: JGit operations failed.", e);
        }
    }

    // codeDir must not contain files, similar to the Git clone command.
    // same codehub uri restrictions as pushToGit()

    private static void cloneGitRepo(final File codeDir, final Manifest manifest) throws CodegenException {

        // this is to support codegen to either github or codehub
        Feature gitFeature = manifest.getFeatureByName("github");

        if (gitFeature == null) {
            gitFeature = manifest.getFeatureByName("codehub");
        }

        if (gitFeature == null) {
            throw new BadRequestException("no github or codehub feature in manifest");
        }

        final String vcsUri = gitFeature.getUri();

        if (vcsUri == null || vcsUri.isEmpty()) {
            throw new BadRequestException("no github or codehub feature URI in manifest");
        }

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            throw new BadRequestException("user not authenticated");
        }

        try {
            final Git git = Git.cloneRepository().setURI(vcsUri)
                .setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(auth.getName(), (String) auth.getCredentials()))
                .setDirectory(codeDir).call();
            git.close();
        } catch (GitAPIException e) {
            throw new GitPushException("pushToGitRepo: JGit operations failed.", e);
        }
    }
}

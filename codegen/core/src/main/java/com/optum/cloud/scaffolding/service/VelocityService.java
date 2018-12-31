package com.optum.cloud.scaffolding.service;

import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.cloud.scaffolding.utilities.ResourceFileUtilities;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.CodeHubFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.GitHubFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.MarathonFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.OpenShiftFeature;
import org.apache.velocity.VelocityContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class VelocityService {

    public static void generate(final String template, final HashMap<String, String> includePaths, final Path dest, final Manifest manifest) throws IOException {
        // Create velocity context
        final VelocityContext context = new VelocityContext();
        String originalAppName = manifest.getApplication().getName();
        String teamName = manifest.getApplication().getTeam().replaceAll("(\\W)", "");
        String appName = manifest.getApplication().getName().replaceAll("(\\W)", "");
        String identifier = manifest.getIdentifier() != null ? manifest.getIdentifier() : "";
        String description = manifest.getDescription() != null ? manifest.getDescription() : "";
        String askId = manifest.getBillingInformation().getAskGlobalId();
        String oseCluster = "ocp-ctc-core-nonprod.optum.com";

        context.put("groupId", String.format("com.optum.%s.%s", teamName, appName));
        context.put("artifactId", appName);
        context.put("originalAppName", originalAppName);
        context.put("package", String.format("com.optum.%s.%s", teamName, appName));
        context.put("team", teamName);
        context.put("appName", appName);
        context.put("oseCluster", oseCluster);

        // Enable for Service Catalog
        // context.put("projectOwner", manifest.getApplication().getProjectOwner().toLowerCase() == "esc_integration" ? "jumpstart" : manifest.getApplication().getProjectOwner());
        context.put("projectOwner", manifest.getApplication().getProjectOwner());

        if (askId == null || askId.isEmpty()) {
            askId = "poc";
            context.put("askId", askId);
        } else {
            context.put("askId", askId);
        }

        // support either codehub or github repos
        final GitHubFeature github = (GitHubFeature) manifest.getFeatureByName("github");

        if (github != null) {
            context.put("github_url", github.getUri() + ".git");
        } else {
            final CodeHubFeature codehub = (CodeHubFeature) manifest.getFeatureByName("codehub");
            if (codehub != null) {
                context.put("github_url", codehub.getUri() + "." + codehub.getScmType());
            }
        }

        // support either marathon or openshift
        final OpenShiftFeature ose = (OpenShiftFeature) manifest.getFeatureByName("openshift");

        if (ose != null) {
            context.put("paas_server", ose.getUri());
            context.put("paas_project", ose.getProjectName().toLowerCase());
            context.put("friendly_url", "http://" + originalAppName + "-" + teamName + "." + oseCluster);
        } else {
            final MarathonFeature marathon = (MarathonFeature) manifest.getFeatureByName("marathon");
            if (marathon != null) {
                context.put("paas_server", marathon.getUri());
                context.put("paas_project", marathon.getServiceName().toLowerCase());
                context.put("friendly_url", "");
            }
        }

        // OPI API Catalog support
        context.put("api_name", appName);
        context.put("identifier", identifier);
        context.put("description", description);
        if (github != null) {
            context.put("repository_name", "GitHub");
            context.put("repository_url", github.getUri());
            context.put("properties_url", github.getUri() + "/blob/master/properties.json");
        } else {
            context.put("repository_name", "Repository Name");
            context.put("repository_url", "Repository URL");
        }
        try {
            Path fullTemplate = Paths.get(System.getProperty("java.io.tmpdir"), Long.toString(System.currentTimeMillis()));
            ResourceFileUtilities.doRecursiveCopyOfResourceFolder(template, fullTemplate);

            for (String include : includePaths.keySet()) {
                if (includePaths.get(include) != null) {
                    ArrayList<String> includeChunk = ResourceFileUtilities.getResourceFileAsList(includePaths.get(include) + "/include.txt");
                    String formattedInclude = "";
                    for (String piece : includeChunk) {
                        formattedInclude += piece + "\n";
                    }
                    context.put(include, formattedInclude);

                    String copyFilesPath = includePaths.get(include) + ("/copyFiles");
                    if (ResourceFileUtilities.resourceFileExists(copyFilesPath)) {
                        ResourceFileUtilities.doRecursiveCopyOfResourceFolder(copyFilesPath, fullTemplate);
                    }
                } else {
                    context.put(include, "");
                }
            }

            Files.walkFileTree(fullTemplate, new VelocityTemplateVisitor(dest, context));
        } catch (URISyntaxException u) {
            System.out.println("URI Issues.");
        }
    }

}

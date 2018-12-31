package com.optum.cloud.scaffolding.service;

import com.optum.cloud.scaffolding.utilities.ResourceFileUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MetadataService {

    private static final String INCLUDESROOT = "/velocity/includes/";

    public static ArrayList<String> getAvailableTypes() throws IOException {
        return ResourceFileUtilities.listResourceFilesIn("/feature");
    }

    public static ArrayList<String> getSubtypesFor(String type) throws IOException {
        if (!getAvailableTypes().contains(type)) {
            throw new IllegalArgumentException(String.format("Given type %s does not exist.", type));
        }
        return ResourceFileUtilities.listResourceFilesIn("/feature/" + type);
    }

    public static ArrayList<String> getPlatformsFor(String type, String subtype) throws IOException {
        if (!getAvailableTypes().contains(type)) {
            throw new IllegalArgumentException(String.format("Given type %s does not exist.", type));
        }
        if (!getSubtypesFor(type).contains(subtype)) {
            throw new IllegalArgumentException(String.format("Given subtype %s does not exist for type %s.", subtype, type));
        }
        ArrayList<String> platforms = ResourceFileUtilities.listResourceFilesIn(String.format("/feature/%s/%s", type, subtype));
        platforms.remove("facets");
        return platforms;
    }

    public static ArrayList<String> getFeaturesFor(String type, String subtype, String paas) throws IOException {
        if (!getAvailableTypes().contains(type)) {
            throw new IllegalArgumentException(String.format("Given type %s does not exist.", type));
        }
        if (!getSubtypesFor(type).contains(subtype)) {
            throw new IllegalArgumentException(String.format("Given subtype %s does not exist for type %s.", subtype, type));
        }
        if (!getPlatformsFor(type, subtype).contains(paas)) {
            throw new IllegalArgumentException(String.format("Given platform %s does not exist for type %s, subtype %s", paas, type, subtype));
        }
        String featureFile = ResourceFileUtilities.getResourceFileAsText(String.format("/feature/%s/%s/%s", type, subtype, paas));
        return new ArrayList<>(Arrays.asList(featureFile.split(",")));
    }

    static ArrayList<String> getFacetsFor(String type, String subtype) throws IOException {
        if (!getAvailableTypes().contains(type)) {
            throw new IllegalArgumentException(String.format("Given type %s does not exist.", type));
        }
        if (!getSubtypesFor(type).contains(subtype)) {
            throw new IllegalArgumentException(String.format("Given subtype %s does not exist for type %s.", subtype, type));
        }
        String featureFile = ResourceFileUtilities.getResourceFileAsText(String.format("/feature/%s/%s/facets", type, subtype));
        return new ArrayList<>(Arrays.asList(featureFile.split(",")));
    }

    static HashMap<String, String> determineIncludes(List<String> desiredTools, String paas) throws IOException {
        HashMap<String, String> availableIncludes = getAllIncludes(paas);
        HashMap<String, String> finalIncludeList = new HashMap<>();

        if (desiredTools.size() > 0) {
            for (String include : availableIncludes.keySet()) {
                if (desiredTools.contains(include)) {
                    finalIncludeList.put(include, availableIncludes.get(include));
                } else {
                    finalIncludeList.put(include, null);
                }
            }
        } else {
            for (String include : availableIncludes.keySet()) {
                finalIncludeList.put(include, null);
            }
        }

        finalIncludeList.put("deploy", availableIncludes.get("deploy"));

        return finalIncludeList;
    }

    static HashMap<String, String> getAllIncludes(String paas) throws IOException {
        ArrayList<String> includes = ResourceFileUtilities.listResourceFilesIn(INCLUDESROOT);
        HashMap<String, String> includesWithPaths = new HashMap<>();

        for (String include : includes) {
            String includePath = INCLUDESROOT + include;
            if (ResourceFileUtilities.resourceFileExists(includePath + "/paas")) {
                includesWithPaths.put(include, includePath + "/" + paas);
            } else
                includesWithPaths.put(include, includePath);
        }
        return includesWithPaths;
    }
}

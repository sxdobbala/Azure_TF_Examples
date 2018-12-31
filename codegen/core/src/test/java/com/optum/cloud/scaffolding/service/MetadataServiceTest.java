package com.optum.cloud.scaffolding.service;

import com.optum.cloud.scaffolding.utilities.ResourceFileUtilities;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class MetadataServiceTest {

    private static final String includesPaas = "openshift";

    @Test
    public void ableToRetrieveListOfAvailableTypes() {
        try {
            assertTrue("Type list returned has at least one element.",
                MetadataService.getAvailableTypes().size() > 0);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test
    public void ableToRetrieveListOfSubtypesGivenForEachType() {
        try {
            for (String type : MetadataService.getAvailableTypes()) {
                assertTrue(String.format("Subtype list for type %s returned has at least one element.", type),
                    MetadataService.getSubtypesFor(type).size() > 0);
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrieveSubtypesDueToInvalidType() {
        try {
            MetadataService.getSubtypesFor("garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test
    public void ableToRetrieveListOfPlatformsGivenForEachTypeAndSubtype() {
        try {
            for (String type : MetadataService.getAvailableTypes()) {
                for (String subtype : MetadataService.getSubtypesFor(type)) {
                    assertTrue(String.format("Subtype list for type %s returned has at least one element.", type),
                        MetadataService.getPlatformsFor(type, subtype).size() > 0);
                }
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrievePlatformsDueToInvalidType() {
        try {
            MetadataService.getPlatformsFor("garbage", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrievePlatformsDueToInvalidSubtype() {
        try {
            MetadataService.getPlatformsFor("peds", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test
    public void ableToRetrieveListOfFeaturesGivenValidParams() {
        try {
            for (String type : MetadataService.getAvailableTypes()) {
                for (String subtype : MetadataService.getSubtypesFor(type)) {
                    for (String paas : MetadataService.getPlatformsFor(type, subtype)) {
                        assertTrue("Feature list returned has at least one element.",
                            MetadataService.getFeaturesFor("peds", "microservice", "openshift").size() > 0);
                    }
                }
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrieveFeaturesDueToInvalidType() {
        try {
            MetadataService.getFeaturesFor("garbage", "garbage", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrieveFeaturesDueToInvalidSubtype() {
        try {
            MetadataService.getFeaturesFor("peds", "garbage", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrieveFeaturesDueToInvalidPaas() {
        try {
            MetadataService.getFeaturesFor("peds", "microservice", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test
    public void ableToRetrieveListOfFacetsGivenValidParams() {
        try {
            for (String type : MetadataService.getAvailableTypes()) {
                for (String subtype : MetadataService.getSubtypesFor(type)) {
                    assertTrue("Facet list returned has at least one element.",
                        MetadataService.getFacetsFor(type, subtype).size() > 0);
                }
            }

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrieveFacetsDueToInvalidType() {
        try {
            MetadataService.getFacetsFor("garbage", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void unableToRetrieveFacetsDueToInvalidSubtype() {
        try {
            MetadataService.getFacetsFor("peds", "garbage");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Test
    public void checkAllIncludes() throws IOException {
        HashMap<String, String> result = MetadataService.getAllIncludes(includesPaas);

        assertTrue("GetAllIncludes should be returning at least the deployment.", result.size() > 0);

        for (String include : result.keySet()) {
            String includePath = result.get(include);
            assertTrue("The following include doesn't have an include.txt available: " + include, ResourceFileUtilities.resourceFileExists(includePath + "/include.txt"));
        }
    }

    @Test
    public void determineIncludesForFullToolset() throws IOException {
        ArrayList<String> allTools = new ArrayList<>(MetadataService.getAllIncludes(includesPaas).keySet());
        allTools.remove("deploy");

        HashMap<String, String> result = MetadataService.determineIncludes(allTools, includesPaas);

        assertTrue("Result should contain the same amount of tools as allTools, plus 1 for deploy.", result.size() == allTools.size() + 1);
    }

    @Test
    public void determineIncludesForPartialToolset() throws IOException {
        ArrayList<String> partialTools = new ArrayList<>();
        partialTools.add("fortify");

        HashMap<String, String> result = MetadataService.determineIncludes(partialTools, includesPaas);

        int validIncludes = 0;
        for (String include : result.keySet()) {
            if (result.get(include) != null) validIncludes++;
        }

        assertTrue("Result should contain only 2 valid includes", validIncludes == 2);
        assertTrue("Result should have a valid fortify path", ResourceFileUtilities.resourceFileExists(result.get("fortify")));
        assertTrue("Result should have a valid deploy path", ResourceFileUtilities.resourceFileExists(result.get("deploy")));
    }

    @Test
    public void determineIncludesForEmptyToolset() throws IOException {
        ArrayList<String> noTools = new ArrayList<>();

        HashMap<String, String> result = MetadataService.determineIncludes(noTools, includesPaas);

        int validIncludes = 0;
        for (String include : result.keySet()) {
            if (result.get(include) != null) validIncludes++;
        }

        assertTrue("Result should contain 1 valid include", validIncludes == 1);
        assertTrue("Result should have a valid deploy path", ResourceFileUtilities.resourceFileExists(result.get("deploy")));
    }
}

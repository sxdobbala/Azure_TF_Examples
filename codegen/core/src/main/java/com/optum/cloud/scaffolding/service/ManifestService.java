package com.optum.cloud.scaffolding.service;

import com.optum.cloud.scaffolding.exception.BadRequestException;
import com.optum.cloud.scaffolding.exception.CodegenException;
import com.optum.pbi.devops.toolchain.service.model.codegen.FeatureFactory;
import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.CodeGenFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.Feature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.JenkinsFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.facets.Facet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManifestService {

    public static Manifest generateManifestFrom(Manifest manifest) throws IOException, CodegenException {
        if (manifest.getApplication() == null) {
            throw new BadRequestException("Must provide an Application");
        }

        String appType = "";
        String appSubType = "";
        String paas = "";

        if (manifest.getApplication().getApplicationType() != null && MetadataService.getAvailableTypes().contains(manifest.getApplication().getApplicationType())) {
            appType = manifest.getApplication().getApplicationType();
        } else {
            throw new BadRequestException("Application must contain a valid app type.");
        }

        if (manifest.getApplication().getApplicationType() != null && MetadataService.getSubtypesFor(appType).contains(manifest.getApplication().getSubType())) {
            appSubType = manifest.getApplication().getSubType();
        } else {
            throw new BadRequestException("Application must contain a valid app subtype.");
        }

        if (manifest.getPaas() != null)
            paas = manifest.getPaas();
        else
            paas = "openshift";

        final List<Feature> featureList = getFeatureListFor(appType, appSubType, paas);

        for (Feature feature : featureList) {
            if (feature.getName().toLowerCase().equals("jenkins")) {
                JenkinsFeature jenkins = (JenkinsFeature) feature;
                HashMap<String, String> fullIncludeSet = MetadataService.getAllIncludes(paas);
                fullIncludeSet.remove("deploy");
                ArrayList<String> allTools = new ArrayList<>();
                allTools.addAll(fullIncludeSet.keySet());
                jenkins.setTools(allTools);
            }
        }

        Manifest response = new Manifest();
        response.setApplication(manifest.getApplication());
        response.setBillingInformation(manifest.getBillingInformation());
        response.setFeatures(featureList);
        response.setPaas(manifest.getPaas());
        response.setVersion(manifest.getVersion());

        return response;
    }

    private static List<Feature> getFeatureListFor(String appType, String appSubType, String paas) throws IOException {
        ArrayList<String> featureNames = MetadataService.getFeaturesFor(appType, appSubType, paas);

        final List<Feature> featureList = new ArrayList<>();
        for (String featureName : featureNames) {
            final Feature feature = FeatureFactory.valueOf(featureName.toUpperCase()).create();
            if (featureName.equals("codegen")) {
                final CodeGenFeature codegenFeature = (CodeGenFeature) feature;
                ArrayList<Facet> facets = new ArrayList<>();
                for (String facet : MetadataService.getFacetsFor(appType, appSubType)) {
                    String[] facetPieces = facet.split("@");
                    if (facetPieces.length == 2) {
                        facets.add(new Facet(facetPieces[0], facetPieces[1]));
                    }
                }
                codegenFeature.setFacets(facets);
            }
            featureList.add(feature);
        }
        return featureList;
    }
}

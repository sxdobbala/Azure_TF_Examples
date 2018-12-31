package com.optum.cloud.scaffolding.testutils;

import com.optum.pbi.devops.toolchain.service.model.codegen.*;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.Feature;

import java.util.ArrayList;

public class Generator {

    private static final String VELOCITYROOT = "/velocity/";

    public static Manifest generateDefaultManifest(String appName, String teamName, String paas) {
        Application application = new Application();
        application.setName(appName);
        application.setTeam(teamName);
        application.setApplicationType("peds");
        application.setSubType("microservice");

        Manifest manifest = new Manifest();
        manifest.setBillingInformation(generateBillingInfo());
        manifest.setApplication(application);
        manifest.setPaas(paas);
        manifest.setFeatures(new ArrayList<Feature>());

        return manifest;
    }

    private static BillingInformation generateBillingInfo() {
        BillingInformation billingInfo = new BillingInformation();
        billingInfo.setAskGlobalId("UHGWM110-000000");
        billingInfo.setTmdbNumber("TMDB-0000000");
        return billingInfo;
    }
}

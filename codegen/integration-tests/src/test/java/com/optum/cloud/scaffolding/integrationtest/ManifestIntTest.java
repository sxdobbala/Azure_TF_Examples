package com.optum.cloud.scaffolding.integrationtest;

import com.optum.pbi.devops.toolchain.service.model.codegen.Application;
import com.optum.pbi.devops.toolchain.service.model.codegen.BillingInformation;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.optum.pbi.devops.toolchain.service.model.codegen.Manifest;

@RunWith(MockitoJUnitRunner.class)
public class ManifestIntTest extends BaseAdaptor {
    private Map<String, String> headers;
    private static String authString;

    @BeforeClass
    public static void setup() throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = CodeGenIntTest.class.getResourceAsStream("/application-secrets.yml");

        properties.load(inputStream);
        String pbiuser = properties.getProperty("com.optum.pbi.real.username");
        String password = properties.getProperty("com.optum.pbi.real.password");

        authString = pbiuser + ":" + password;
        inputStream.close();
    }

    @Before
    public void beforeTest() throws UnsupportedEncodingException {
        headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString(authString.getBytes("utf-8")));
    }

    @Test
    public void manifestGenerationSuccessfulGivenValidInput() {
        BillingInformation billInfo = new BillingInformation("TMDB-0000000", "UHGWM110-000000");
        Application testApp = new Application("projectName", "projectteam", "projectOwner", "peds", "microservice");
        Manifest manifest = new Manifest("v1", "openshift", testApp, billInfo, new ArrayList<>());
        CloseableHttpResponse response = postWithHeadersReturnHttpResponse("/api/v1/manifest/generate", manifest, headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns OK", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void manifestGenerationFailsProperlyDueToInvalidInput() {
        Manifest manifest = new Manifest();
        CloseableHttpResponse response = postWithHeadersReturnHttpResponse("/api/v1/manifest/generate", manifest, headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns Bad Request", response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

}

package com.optum.cloud.scaffolding.integrationtest;

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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class MetadataIntTest extends BaseAdaptor {
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
    public void ableToRetrieveListOfAvailableTypes() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/types", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns OK", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void ableToRetrieveSubtypesWithValidType() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/peds/subtypes", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns OK", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void ableToRetrieveListOfFeaturesGivenValidParams() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/peds/microservice/openshift/features", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns OK", response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void unableToRetrieveSubtypesDueToInvalidType() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/nonexistant/subtypes", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns Bad Request", response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void unableToRetrieveFeaturesDueToInvalidType() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/nonexistant/microservice/openshift/features", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns Bad Request", response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void unableToRetrieveFeaturesDueToInvalidSubtype() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/peds/nonexistant/openshift/features", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns Bad Request", response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void unableToRetrieveFeaturesDueToInvalidPaas() {
        CloseableHttpResponse response = getWithHeadersReturnHttpResponse("/api/v1/metadata/applications/peds/microservice/nonexistant/features", headers);

        Assert.assertNotNull("Response is not null.", response);
        Assert.assertEquals("Response returns Bad Request", response.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }
}

package com.optum.cloud.scaffolding.integrationtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class HealthIntTest extends BaseAdaptor {

    @Test
    public void respondingProperly() {
        String response = get("/api/v1/healthCheck");

        assertTrue(response.contains("Health Check"));
    }

}

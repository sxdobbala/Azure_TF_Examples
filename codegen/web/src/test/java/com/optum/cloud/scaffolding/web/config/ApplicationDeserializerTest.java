package com.optum.cloud.scaffolding.web.config;

import com.optum.pbi.devops.toolchain.service.model.codegen.Application;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@JsonTest
public class ApplicationDeserializerTest {

    @Autowired
    private JacksonTester<Application> json;

    @Test
    public void testDeserialize() throws Exception {
        Application application = new Application();
        application.setApplicationType("peds");
        application.setSubType("microservice");
        application.setName("demoapp");
        application.setTeam("team");
        application.setProjectOwner("abc123");

        // add/remove 'web' before  when testing locally vs through project root
        // Locally: web/src/test/resources
        // codegen root src/test/resources
        String content = readFromFile("src/test/resources/application.json");

        assertNotNull(content);

        Application parsedApp = this.json.parse(content).getObject();

        assertEquals(application.getName(), parsedApp.getName());
        assertEquals(application.getTeam(), parsedApp.getTeam());
        assertEquals(application.getApplicationType(), parsedApp.getApplicationType());
        assertEquals(application.getSubType(), parsedApp.getSubType());
        assertEquals(application.getProjectOwner(), parsedApp.getProjectOwner());
    }

    private String readFromFile(String filename) {

        if (StringUtils.isEmpty(filename)) {
            return "";
        }

        File f = new File(filename);
        if (!f.exists()) {
            System.out.println(String.format("No such file: %s", f.getAbsolutePath()));
            return "";
        }

        try {
            return FileUtils.readFileToString(f, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}

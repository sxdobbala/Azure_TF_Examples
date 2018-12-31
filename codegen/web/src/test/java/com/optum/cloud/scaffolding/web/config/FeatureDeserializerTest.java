package com.optum.cloud.scaffolding.web.config;

import com.optum.pbi.devops.toolchain.service.model.codegen.features.DefaultFeature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.Feature;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.JenkinsFeature;
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
public class FeatureDeserializerTest {

    @Autowired
    private JacksonTester<Feature> json;

    @Test
    public void testFeatureDeserializeToJenkins() throws Exception {
        JenkinsFeature jenkinsFeature = new JenkinsFeature();
        jenkinsFeature.setJobName("demoapp");

        String content = readFromFile("src/test/resources/jenkinsFeature.json");

        assertNotNull(content);

        assertEquals(JenkinsFeature.class, this.json.parse(content).getObject().getClass());

        JenkinsFeature parsedFeature = (JenkinsFeature) this.json.parse(content).getObject();
        assertEquals(jenkinsFeature.getName(), parsedFeature.getName());
        assertEquals(jenkinsFeature.getJobName(), parsedFeature.getJobName());
        assertEquals("v1", parsedFeature.getVersion());
    }

    @Test
    public void testFeatureDeserializeToNewFeature() throws Exception {
        String content = readFromFile("src/test/resources/unknownFeature.json");

        assertNotNull(content);

        assertEquals(DefaultFeature.class, this.json.parse(content).getObject().getClass());
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

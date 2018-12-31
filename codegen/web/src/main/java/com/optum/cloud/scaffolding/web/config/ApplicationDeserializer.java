package com.optum.cloud.scaffolding.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.optum.pbi.devops.toolchain.service.model.codegen.Application;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;

@JsonComponent
public class ApplicationDeserializer extends JsonObjectDeserializer<Application> {

    @Override
    protected Application deserializeObject(final JsonParser jsonParser, final DeserializationContext context, final ObjectCodec codec,
                                            final JsonNode tree) throws IOException {

        String applicationType = null;
        String applicationSubType = null;

        final String appTypeKey = "applicationType";
        final String appSubTypeKey = "subType";

        if (tree.has(appTypeKey)) {
            final String appType = tree.get(appTypeKey).asText();
            applicationType = appType.toLowerCase();
        }

        if (tree.has(appSubTypeKey)) {
            final String subType = tree.get(appSubTypeKey).asText();
            applicationSubType = subType.toLowerCase();
        }

        final Application app = new Application();
        app.setName(tree.get("name").asText());
        app.setTeam(tree.get("team").asText());
        app.setProjectOwner(tree.get("projectOwner").asText());
        app.setApplicationType(applicationType);
        app.setSubType(applicationSubType);

        return app;
    }
}

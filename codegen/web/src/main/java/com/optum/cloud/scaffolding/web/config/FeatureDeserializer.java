package com.optum.cloud.scaffolding.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.optum.pbi.devops.toolchain.service.model.codegen.features.Feature;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@JsonComponent
public class FeatureDeserializer extends JsonObjectDeserializer<Feature> {

    private static final String FEATURE_PACKAGE_LOOKUP = "com.optum.pbi.devops.toolchain.service.model";
    private static final String FEATURE_NAME_STRING = "com.optum.pbi.devops.toolchain.service.model.%s.features.%sfeature";
    //    private static Logger logger = LoggerFactory.getLogger(JsonCustomDeserializer.class);
    private Map<String, Class<? extends Feature>> featureMap;

    public FeatureDeserializer() {
        featureMap = new HashMap<>();

        final Reflections reflections = new Reflections(FEATURE_PACKAGE_LOOKUP);
        final Set<Class<? extends Feature>> featureClassSet = reflections.getSubTypesOf(Feature.class);
        for (Class<? extends Feature> featureClass : featureClassSet) {
            featureMap.put(featureClass.getName().toLowerCase(), featureClass);
        }
    }

    @Override
    protected Feature deserializeObject(final JsonParser jsonParser, final DeserializationContext context, final ObjectCodec codec,
                                        final JsonNode tree) throws IOException {

        Class<? extends Feature> featureClass = null;
        Feature feature = null;

        String featureName = null;
        String version = null;

        if (tree.has("name")) {
            final JsonNode node = tree.get("name");
            featureName = node.asText();
        }

        if (tree.has("version")) {
            final JsonNode versionNode = tree.get("version");
            if (versionNode != null) {
                version = versionNode.asText();
                if ("null".equals(version) || StringUtils.isBlank(version) || version == null) {
                    version = "v1";
                }
            }

        }
        if (featureName != null) {
            final String featureClassName = String.format(FEATURE_NAME_STRING, version, featureName.toLowerCase());
            featureClass = featureMap.get(featureClassName);
        }

        if (featureClass == null) {
            throw new IllegalArgumentException("Unable to parse feature as given");
        } else {
            feature = codec.treeToValue(tree, featureClass);
            feature.setVersion("v1");
        }

        return feature;
    }
}

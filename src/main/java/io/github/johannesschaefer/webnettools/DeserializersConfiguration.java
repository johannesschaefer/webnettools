package io.github.johannesschaefer.webnettools;

import io.quarkus.jsonb.JsonbConfigCustomizer;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

@Singleton
public class DeserializersConfiguration implements JsonbConfigCustomizer {
    @Inject
    Reflections reflections;

    @Override
    public void customize(JsonbConfig jsonbConfig) {
        jsonbConfig.withDeserializers(new PayloadDeserializer(reflections));
    }
}
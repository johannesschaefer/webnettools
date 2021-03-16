package io.github.johannesschaefer.webnettools;

import com.google.common.collect.Maps;
import io.github.johannesschaefer.webnettools.annotation.Tool;
import io.github.johannesschaefer.webnettools.payload.Payload;
import org.reflections.Reflections;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.Map;

public class PayloadDeserializer implements JsonbDeserializer<Payload> {
    private final Jsonb jsonb = JsonbBuilder.create();
    Map<String, Class<? extends Payload>> tools = Maps.newHashMap();

    public PayloadDeserializer(Reflections reflections) {
        var payloadTypes = reflections.getSubTypesOf(Payload.class);
        for (Class<? extends Payload> p : payloadTypes) {
            if (p.getDeclaredAnnotation(Tool.class) != null) {
                tools.put(p.getDeclaredAnnotation(Tool.class).name(), p);
            }
        }
    }

    @Override
    public Payload deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type rtType) {
        JsonObject value = jsonParser.getObject();
        String type = value.getString("type", "null");
        return jsonb.fromJson(value.toString(), classFor(type));
    }

    private Class<? extends Payload> classFor(String type) {
        if (!tools.containsKey(type)) {
            throw new JsonbException("unknown shape type " + type);
        }

        return tools.get(type);
    }
}
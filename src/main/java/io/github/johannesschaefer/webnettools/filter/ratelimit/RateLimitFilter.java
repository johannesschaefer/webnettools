package io.github.johannesschaefer.webnettools.filter.ratelimit;

import io.github.johannesschaefer.webnettools.payload.*;
import io.vertx.core.http.HttpServerRequest;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

@Provider
public class RateLimitFilter implements ContainerRequestFilter {
    private static final Logger LOG = Logger.getLogger(RateLimitFilter.class);

    @Inject
    @RateLimitCache
    private Map<String, Boolean> rateLimitCache;

    @Context
    private UriInfo info;

    @Context
    private HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        if (!info.getPath().startsWith("/tools/")) {
            return;
        }
        // check if client exceeded rate limit
        if (isOverRateLimit(context)) {
            context.abortWith(Response.status(429).entity("Too Many Requests").build());
        }
    }

    private boolean isOverRateLimit(ContainerRequestContext context) {
        String content = "";
        content += info.getPath();
        content += " ";
        content += request.remoteAddress().host();
        content += " ";
        content += getCacheValue(context);

        if(rateLimitCache.containsKey(content)) {
            return true;
        }

        rateLimitCache.put(content, true);
        return false;
    }

    private static class PayloadDeserializer implements JsonbDeserializer<Payload> {

        private static final Jsonb jsonb = JsonbBuilder.create();

        @Override
        public Payload deserialize(JsonParser jsonParser, DeserializationContext deserializationContext, Type rtType) {
            JsonObject jsonObj = jsonParser.getObject();
            String jsonString = jsonObj.toString();
            String type = jsonObj.getString("type");

            switch (type) {
                case "ping":
                    return jsonb.fromJson(jsonString, PingPayload.class);
                case "nmap":
                    return jsonb.fromJson(jsonString, NmapPayload.class);
                case "testssl":
                    return jsonb.fromJson(jsonString, TestSSLPayload.class);
                case "traceroute":
                    return jsonb.fromJson(jsonString, TraceroutePayload.class);
                default:
                    throw new JsonbException("Unknown type: " + type);
            }
        }
    }

    private String getCacheValue(ContainerRequestContext request) {
        try {
            String json = IOUtils.toString(request.getEntityStream(), Charsets.UTF_8);
            InputStream in = IOUtils.toInputStream(json);
            request.setEntityStream(in);

            JsonbConfig cfg = new JsonbConfig().withDeserializers(new PayloadDeserializer());
            Jsonb jsonb = JsonbBuilder.create(cfg);
            Payload payload = jsonb.fromJson(json, Payload.class);
            return payload.getCacheString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
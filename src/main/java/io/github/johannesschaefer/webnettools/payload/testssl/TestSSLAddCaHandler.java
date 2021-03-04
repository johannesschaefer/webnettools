package io.github.johannesschaefer.webnettools.payload.testssl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.annotation.ServerParamHandler;
import io.github.johannesschaefer.webnettools.payload.Payload;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class TestSSLAddCaHandler implements ServerParamHandler {
    @Override
    public Collection<String> handle(Field field, Payload payload) {
        if (!(payload instanceof TestSSLPayload)) {
            throw new RuntimeException("Payload is not of type TestSSLPayload.");
        }
        // if add ca file is set, ignore the server settings
        if(!Strings.isNullOrEmpty(((TestSSLPayload) payload).getAddCaFile())) {
            return Lists.newArrayList();
        }
        if(Files.exists(Paths.get("/certs"))) {
            return Lists.newArrayList("--add-ca", "/certs");
        }
        return Lists.newArrayList();
    }
}
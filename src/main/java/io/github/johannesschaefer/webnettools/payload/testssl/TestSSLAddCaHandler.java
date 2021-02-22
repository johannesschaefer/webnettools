package io.github.johannesschaefer.webnettools.payload.testssl;

import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.annotation.ServerParamHandler;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class TestSSLAddCaHandler implements ServerParamHandler {
    @Override
    public Collection<String> handle(Field field) {
        if(Files.exists(Paths.get("/certs"))) {
            return Lists.newArrayList("--add-ca", "/certs");
        }
        return Lists.newArrayList();
    }
}
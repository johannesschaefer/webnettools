package io.github.johannesschaefer.webnettools.payload.testssl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.annotation.ServerParamHandler;
import io.github.johannesschaefer.webnettools.payload.Payload;
import io.quarkus.arc.Unremovable;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

@Unremovable
@ApplicationScoped
public class TestSSLAddCaHandler implements ServerParamHandler {
    @Inject
    Logger log;

    @ConfigProperty(name = "CA_DIR", defaultValue = "/certs")
    String certDir;

    @Override
    public Collection<String> handle(Field field, Payload payload) {
        if (!(payload instanceof TestSSLPayload)) {
            throw new RuntimeException("Payload is not of type TestSSLPayload.");
        }
        // if add ca file is set, ignore the server settings
        if(!Strings.isNullOrEmpty(((TestSSLPayload) payload).getAddCaFile())) {
            return Lists.newArrayList();
        }

        try {
            if(Files.exists(Paths.get(certDir))) {
                if (Files.list(Paths.get(certDir)).anyMatch(f -> f.toString().toLowerCase().endsWith(".pem"))) {
                    return Lists.newArrayList("--add-ca", certDir);
                }
                else {
                    log.warn("No *.pem files found in directory " + certDir);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Lists.newArrayList();
    }
}
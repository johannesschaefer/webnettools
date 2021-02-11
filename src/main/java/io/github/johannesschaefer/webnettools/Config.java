package io.github.johannesschaefer.webnettools;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.List;

@Path("/config")
public class Config {
    @ConfigProperty(name = "AVAILABLE_TOOLS", defaultValue = "testssl,ping,traceroute,nmap")
    private List<String> availableTools;

    @GET
    @Produces
    @Singleton
    public ToolConfiguration config() throws IOException {
        return new ToolConfiguration(availableTools);
    }
}
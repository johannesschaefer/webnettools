package io.github.johannesschaefer.webnettools;

import com.google.common.collect.Lists;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;

@Path("/config")
public class Config {
    @GET
    public ToolConfiguration config() throws IOException {
        return new ToolConfiguration(Lists.newArrayList("testssl", "ping", "traceroute", "nmap"));
    }
}
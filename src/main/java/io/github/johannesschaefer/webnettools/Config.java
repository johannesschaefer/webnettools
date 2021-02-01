package io.github.johannesschaefer.webnettools;

import io.github.johannesschaefer.webnettools.payload.NmapPayload;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/config")
public class Config {

    @GET
    public ToolConfiguration config() throws IOException {
        return new ToolConfiguration();
    }
}

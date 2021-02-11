package io.github.johannesschaefer.webnettools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.payload.NmapPayload;
import io.github.johannesschaefer.webnettools.payload.PingPayload;
import io.github.johannesschaefer.webnettools.payload.TestSSLPayload;
import io.github.johannesschaefer.webnettools.payload.TraceroutePayload;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Path("/tools/")
public class Tools {
    private static final Logger LOG = Logger.getLogger(Tools.class);

    @Inject
    private ToolConfiguration config;

    @POST
    @Path("nmap")
    public Response nmap(NmapPayload payload) throws IOException {
        if (!config.getAvailableTools().contains("nmap")) {
            return Response.serverError().entity("nmap not in list of available tools").build();
        }
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().entity("empty host").build();
        }

        List<String> cmd = Lists.newArrayList("nmap");
        cmd.add(payload.getHost());
        return getStreamResponse(cmd);
    }

    @POST
    @Path("ping")
    public Response ping(PingPayload payload) throws IOException {
        if (!config.getAvailableTools().contains("ping")) {
            return Response.serverError().entity("ping not in list of available tools").build();
        }
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().entity("empty host").build();
        }
        if (payload.getCount() == null) {
            payload.setCount(3);
        }
        if (payload.getCount() < 1) {
            payload.setCount(1);
        }
        if (payload.getCount()> 100) {
            payload.setCount(100);
        }
        if (payload.getCount() == null) {
            payload.setWait(1.);
        }
        if(payload.getWait()< 0.1) {
            payload.setWait(0.1);
        }
        if(payload.getWait()>60.) {
            payload.setWait(60.);
        }

        List<String> cmd = Lists.newArrayList("ping");
        cmd.add("-c");
        cmd.add(String.valueOf(payload.getCount()));
        cmd.add("-i");
        cmd.add(String.valueOf(payload.getWait()));
        cmd.add(payload.getHost());

        return getStreamResponse(cmd);
    }

    @POST
    @Path("traceroute")
    public Response traceroute(TraceroutePayload payload) throws IOException {
        if (!config.getAvailableTools().contains("traceroute")) {
            return Response.serverError().entity("traceroute not in list of available tools").build();
        }
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().entity("empty host").build();
        }

        List<String> cmd = Lists.newArrayList("traceroute");
        cmd.add(payload.getHost());

        return getStreamResponse(cmd);
    }

    @POST
    @Path("testssl")
    public Response testssl(TestSSLPayload payload) throws IOException {
        if (!config.getAvailableTools().contains("testssl")) {
            return Response.serverError().entity("testssl not in list of available tools").build();
        }
        if (payload == null || Strings.isNullOrEmpty(payload.getUrl())) {
            return Response.serverError().entity("empty url").build();
        }

        List<String> cmd = Lists.newArrayList("testssl.sh");
        if (payload.isHints()) {
            cmd.add("--hints");
        }
        if (payload.isQuiet()) {
            cmd.add("--quiet");
        }
        if(Files.exists(Paths.get("/certs"))) {
            cmd.add("--add-ca");
            cmd.add("/certs");
        }
        cmd.add(payload.getUrl());

        return getStreamResponse(cmd);
    }

    private Response getStreamResponse(List<String> cmd) throws IOException {
        LOG.debug("command: " + cmd.toString());
        Process process = new ProcessBuilder().command(cmd).redirectErrorStream(true).start();
        return Response.ok((StreamingOutput) outputStream -> copyStream(process.getInputStream(), outputStream)).build();
    }

    void copyStream(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
            target.flush();
        }
    }
}
package io.github.johannesschaefer.webnettools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.payload.NmapPayload;
import io.github.johannesschaefer.webnettools.payload.PingPayload;
import io.github.johannesschaefer.webnettools.payload.TestSSLPayload;
import io.github.johannesschaefer.webnettools.payload.TraceroutePayload;
import org.jboss.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.List;

@Path("/tools/")
public class Tools {
    private static final Logger LOG = Logger.getLogger(Tools.class);

    @POST
    @Path("nmap")
    public Response nmap(NmapPayload payload) throws IOException {
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().build();
        }

        List<String> cmd = Lists.newArrayList("nmap");
        cmd.add(payload.getHost());
        return getStreamResponse(cmd);
    }

    @POST
    @Path("ping")
    public Response ping(PingPayload payload) throws IOException {
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().build();
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
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().build();
        }

        List<String> cmd = Lists.newArrayList("traceroute");
        cmd.add(payload.getHost());

        return getStreamResponse(cmd);
    }

    @POST
    @Path("testssl")
    public Response testssl(TestSSLPayload payload) throws IOException {
        if (payload == null || Strings.isNullOrEmpty(payload.getUrl())) {
            return Response.serverError().build();
        }

        List<String> cmd = Lists.newArrayList("testssl.sh");
        if (payload.isHints()) {
            cmd.add("--hints");
        }
        if (payload.isQuiet()) {
            cmd.add("--quiet");
        }
        cmd.add(payload.getUrl());

        return getStreamResponse(cmd);
    }

    private Response getStreamResponse(List<String> cmd) throws IOException {
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
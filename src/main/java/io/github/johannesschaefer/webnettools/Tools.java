package io.github.johannesschaefer.webnettools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.payload.PingPayload;
import io.github.johannesschaefer.webnettools.payload.TestSSLPayload;
import io.github.johannesschaefer.webnettools.payload.TraceroutePayload;
import org.jboss.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.List;

@Path("/")
public class Tools {
    private static final Logger LOG = Logger.getLogger(Tools.class);

    @POST
    @Path("ping")
    public Response ping(PingPayload payload) throws IOException {
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().build();
        }
        if (payload.getCount() == null) {
            payload.setCount(3);
        }

        List<String> cmd = Lists.newArrayList("ping");
        cmd.add("-c");
        cmd.add(String.valueOf(payload.getCount()));
        cmd.add(payload.getHost());

        return getStreamResponse(new ProcessBuilder(cmd.toArray(new String[0])));
    }

    @POST
    @Path("traceroute")
    public Response traceroute(TraceroutePayload payload) throws IOException {
        if (payload == null || Strings.isNullOrEmpty(payload.getHost())) {
            return Response.serverError().build();
        }

        List<String> cmd = Lists.newArrayList("traceroute");
        cmd.add(payload.getHost());

        return getStreamResponse(new ProcessBuilder(cmd.toArray(new String[0])));
    }

    @POST
    @Path("testssl")
    public Response testssl(TestSSLPayload payload) throws IOException {
        if (payload == null || Strings.isNullOrEmpty(payload.getUrl())) {
            return Response.serverError().build();
        }

        List<String> cmd = Lists.newArrayList("/testssl.sh-3.0.4/testssl.sh");
        if (payload.isHints()) {
            cmd.add("--hints");
        }
        if (payload.isQuiet()) {
            cmd.add("--quiet");
        }
        cmd.add(payload.getUrl());

        return getStreamResponse(new ProcessBuilder(cmd.toArray(new String[0])));
    }

    private Response getStreamResponse(ProcessBuilder pb) throws IOException {
        var p = pb.start();

        StreamingOutput s = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                copyStream(p.getInputStream(), outputStream);
                copyStream(p.getErrorStream(), outputStream);
            }
        };

        return Response.ok(s).build();
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
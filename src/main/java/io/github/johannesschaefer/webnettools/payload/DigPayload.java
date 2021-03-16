package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.*;
import lombok.Data;

@Data
@Tool(name="dig", displayName="Dig", cmd="dig", description="DNS lookup utility")
public class DigPayload implements Payload {
    @MainParameter(displayName ="Server", description="The name or IP address of the name server to query. This can be an IPv4 address in dotted-decimal notation or an IPv6 address in colon-delimited notation. When the supplied server argument is a hostname, dig resolves that name before querying that name server. If no server argument is provided, dig consults /etc/resolv.conf and queries the name servers listed there. The reply from the name server that responds is displayed.")
    private String server;

    @Override
    public String getCacheString() {
        return server;
    }
}
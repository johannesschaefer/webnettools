package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.BooleanParam;
import io.github.johannesschaefer.webnettools.annotation.MainParameter;
import io.github.johannesschaefer.webnettools.annotation.Tool;
import lombok.Data;

@Data
@Tool(name="nmap", displayName = "Nmap", cmd="nmap", description="<a href=\"https://nmap.org\">Nmap</a> (\"Network Mapper\") is a free and open source utility for network discovery and security auditing.")
public class NmapPayload implements Payload {
    @MainParameter(displayName ="IP / Hostname", description="nmap host")
    private String host;

    @BooleanParam(displayName ="OS detection", param="-O", description="Enable OS detection")
    private Boolean o;

    @Override
    public String getCacheString() {
        return host;
    }
}
package io.github.johannesschaefer.webnettools.payload.nmap;

import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.payload.Payload;
import lombok.Data;

@Data
@Tool(name="nmap", displayName = "Nmap", cmd="nmap", description="<a href=\"https://nmap.org\">Nmap</a> (\"Network Mapper\") is a free and open source utility for network discovery and security auditing.")
public class NmapPayload implements Payload {
    @MainParameter(displayName ="IP / Hostname", description="nmap host")
    private String host;

    @BooleanParam(displayName ="OS detection", param="-O", description="Enable OS detection")
    private Boolean o;

    @BooleanParam(displayName ="Hosts online", param="-Pn", description="Treat all hosts as online -- skip host discovery")
    private Boolean pn;

    @BooleanParam(displayName ="UDP scan", param="-sU", description = "UDP scan")
    private Boolean scanudp;

    @NumberParam(displayName = "Top Ports", param = "--top-ports", description = "Scan <number> most common ports", min = 1, max = 100)
    private Integer topPorts;

    @StringParam(displayName = "Ports", param = "-p", description = "Only scan specified ports\nEx: -p22; -p1-65535; -p U:53,111,137,T:21-25,80,139,8080,S:9")
    private String p;

    @EnumParam(displayName = "Verbose", param = "", description = "Increase verbosity level (use -vv or more for greater effect)", paramType = ParameterType.ONLY_VALUE)
    private VerboseMode v;

    @Override
    public String getCacheString() {
        return host;
    }
}
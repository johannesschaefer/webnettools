package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.MainParameter;
import io.github.johannesschaefer.webnettools.annotation.BooleanParam;
import io.github.johannesschaefer.webnettools.annotation.ServerParam;
import io.github.johannesschaefer.webnettools.annotation.Tool;
import lombok.Data;

@Data
@Tool(name="testssl", displayName="Test SSL", cmd="testssl.sh", description="Check the support of SSL/TLS of any server. Uses the <a href=\"https://testssl.sh\">testssl.sh</a> tooling.")
public class TestSSLPayload implements Payload {
    @MainParameter(displayName ="URL / Hostname", description="URI can be a hostname, an IPv4 or IPv6 address (restriction see below) or an URL. IPv6 addresses need to be in square brackets. For any given parameter port 443 is assumed unless specified by appending a colon and a port number. The only preceding protocol specifier allowed is https. You need to be aware that checks for an IP address might not hit the vhost you want. DNS resolution (A/AAAA record) is being performed unless you have an /etc/hosts entry for the hostname.")
    private String url;

    @BooleanParam(displayName ="Hints", param="--hints", description="This option is not in use yet. This option is meant to give hints how to fix a finding or at least a help to improve something. GIVE_HINTS is the environment variable for this.")
    private Boolean hints;
    @BooleanParam(displayName ="Quiet", param="--quiet", description="Normally testssl.sh displays a banner on stdout with several version information, usage rights and a warning. This option suppresses it. Please note that by choosing this option you acknowledge usage terms and the warning normally appearing in the banner.", defaultValue = true)
    private Boolean quiet;
    @ServerParam(param = "--add-ca", handler = TestSSLAddCaHandler.class)
    private String addCa;

    @Override
    public String getCacheString() {
        return url;
    }
}
package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.*;
import lombok.Data;

@Data
@Tool(name="ping", displayName="Ping", cmd="ping", description="Standard Linux Ping tooling.")
public class PingPayload implements Payload {
    @MainParameter(displayName ="IP / Hostname", description="ping ip")
    private String host;

    @NumberParam(displayName ="Count", param="-c", description="", min=1., max=100., step=1., defaultValue = 3, hasDefaultValue = true)
    private Integer count;
    @NumberParam(displayName ="Wait", param="-i", description="", min=1., max=100., step=1., defaultValue = 1, hasDefaultValue = true)
    private Double wait;
    @BooleanParam(displayName = "Numeric output only", param = "-n", paramType = ParameterType.ONLY_PARAM,description = "Numeric output only.  No attempt will be made to lookup symbolic names for host addresses.")
    private Boolean numericOutputOnly;

    @Override
    public String getCacheString() {
        return host;
    }
}
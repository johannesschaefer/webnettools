package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.FixedParam;
import io.github.johannesschaefer.webnettools.annotation.MainParameter;
import io.github.johannesschaefer.webnettools.annotation.ParameterType;
import io.github.johannesschaefer.webnettools.annotation.Tool;
import lombok.Data;

@Data
@Tool(name="mtr", displayName="MTR", cmd="mtr", description="Network diagnostic tool combining 'traceroute' and 'ping'")
public class MtrPayload implements Payload {
    @MainParameter(displayName ="Hostname")
    private String hostname;

    @FixedParam(param = "--report", paramType = ParameterType.ONLY_PARAM)
    private Boolean report = true;

    @Override
    public String getCacheString() {
        return hostname;
    }
}
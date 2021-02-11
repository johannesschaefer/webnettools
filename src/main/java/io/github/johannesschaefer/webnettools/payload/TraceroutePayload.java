package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

@Data
public class TraceroutePayload implements Payload {
    private String host;

    @Override
    public String getCacheString() {
        return host;
    }
}
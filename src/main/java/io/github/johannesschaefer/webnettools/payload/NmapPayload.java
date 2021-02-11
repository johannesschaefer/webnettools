package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

@Data
public class NmapPayload implements Payload {
    private String host;

    @Override
    public String getCacheString() {
        return host;
    }
}
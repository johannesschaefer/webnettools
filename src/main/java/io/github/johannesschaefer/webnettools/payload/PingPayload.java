package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

@Data
public class PingPayload implements Payload {
    private String host;
    private Integer count;
    private Double wait;

    @Override
    public String getCacheString() {
        return host;
    }
}
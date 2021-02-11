package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

@Data
public class TestSSLPayload implements Payload {
    private String url;
    private boolean hints;
    private boolean quiet;

    @Override
    public String getCacheString() {
        return url;
    }
}
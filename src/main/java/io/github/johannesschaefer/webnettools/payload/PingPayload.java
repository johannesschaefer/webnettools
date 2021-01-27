package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

@Data
public class PingPayload {
    private String host;
    private Integer count;
}

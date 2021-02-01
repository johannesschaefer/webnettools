package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PingPayload implements Payload {
    private String host;
    private Integer count;
    private Double wait;
}

package io.github.johannesschaefer.webnettools.payload;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PingPayload {
    private String host;
    private Integer count;
    private Double wait;
}

package io.github.johannesschaefer.webnettools.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class NumberOptionMD extends OptionMD {
    private double min;
    private double max;
    private double step;
    private Number defaultValue;
}
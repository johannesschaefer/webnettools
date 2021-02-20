package io.github.johannesschaefer.webnettools.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NumberOptionMD implements OptionMD {
    private String name;
    private String displayName;
    private String type;
    private String description;
    private double min;
    private double max;
    private double step;
    private double defaultValue;
}
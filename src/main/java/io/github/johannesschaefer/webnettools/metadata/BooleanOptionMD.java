package io.github.johannesschaefer.webnettools.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooleanOptionMD implements OptionMD {
    private String name;
    private String displayName;
    private String type;
    private String description;
    private Boolean defaultValue;
    private String labelTrue;
    private String labelFalse;
}
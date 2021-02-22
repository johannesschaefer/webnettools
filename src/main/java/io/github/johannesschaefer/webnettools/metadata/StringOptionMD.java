package io.github.johannesschaefer.webnettools.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class StringOptionMD extends OptionMD {
    private String defaultValue;
    private int minlength;
    private int maxlength;
}
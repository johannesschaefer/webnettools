package io.github.johannesschaefer.webnettools.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@NoArgsConstructor
@SuperBuilder
public class EnumOptionMD extends OptionMD {
    private String defaultValue;
    private Map<String, String> values;
}
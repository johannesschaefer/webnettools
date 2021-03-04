package io.github.johannesschaefer.webnettools.metadata;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class FileOptionMD extends OptionMD {
    private int maxSize;
    private String accept;
}
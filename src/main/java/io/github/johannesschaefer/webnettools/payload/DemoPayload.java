package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.*;
import lombok.Data;

@Data
@Tool(name="demo", displayName = "Demo", cmd="echo", description="Demo for the tooling.")
public class DemoPayload implements Payload {
    @MainParameter(displayName ="Main", description="Main parameter for this tool.")
    private String main;

    @NumberParam(displayName ="NumInt", param="num-int", description="number integer demo ", min=0., max=100., step=1., defaultValue = 50, hasDefaultValue = true)
    private Integer numInt;

    @NumberParam(displayName ="NumDouble", param="num-double", description="number double demo ", min=0.1, max=99.9, step=.1, defaultValue = 66.6, hasDefaultValue = true)
    private Double numDouble;

    @StringParam(displayName ="String", param="string", description="String demo", paramType = ParameterType.EQUALS)
    private String string;

    @StringParam(displayName ="String with default", param="string-def", description="String default demo", defaultValue = "test", hasDefaultValue = true)
    private String stringDef;

    @BooleanParam(displayName ="Bool", param="bool1", description="boolean demo")
    private Boolean bool;

    @BooleanParam(displayName ="Bool True", param="bool2", description="boolean demo default true", defaultValue = true, hasDefaultValue = true)
    private Boolean boolTrue;

    @BooleanParam(displayName ="Bool False", param="bool3", description="boolean demo default false", defaultValue = false, hasDefaultValue = true, labelTrue = "On", labelFalse = "Off")
    private Boolean boolFalse;

    @Override
    public String getCacheString() {
        return main;
    }
}
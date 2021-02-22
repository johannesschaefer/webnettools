package io.github.johannesschaefer.webnettools.payload;

import io.github.johannesschaefer.webnettools.annotation.*;
import lombok.Data;

@Data
@Tool(name="demo", displayName = "Demo", cmd="echo", description="Demo for the tooling.")
public class DemoPayload implements Payload {
    @MainParameter(displayName ="Main", description="Main parameter for this tool.")
    private String main;

    @NumberParam(displayName ="NumInt", param="num-int", description="number integer demo ", min=0., max=100., step=1.)
    private Integer numInt = 50;

    @NumberParam(displayName ="NumDouble", param="num-double", description="number double demo ", min=0.1, max=99.9, step=.1)
    private Double numDouble = 66.6;

    @StringParam(displayName ="String", param="string", description="String demo", paramType = ParameterType.EQUALS)
    private String string;

    @StringParam(displayName ="String with default", param="string-def", description="String default demo")
    private String stringDef = "test";

    @BooleanParam(displayName ="Bool", param="bool1", description="boolean demo")
    private Boolean bool;

    @BooleanParam(displayName ="Bool True", param="bool2", description="boolean demo default true")
    private Boolean boolTrue = true;

    @BooleanParam(displayName ="Bool False", param="bool3", description="boolean demo default false", labelTrue = "On", labelFalse = "Off")
    private Boolean boolFalse = false;

    @Override
    public String getCacheString() {
        return main;
    }
}
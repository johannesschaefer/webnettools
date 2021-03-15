package io.github.johannesschaefer.webnettools.payload.speedtest;

import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.payload.Payload;
import lombok.Data;

@Data
@Tool(name="speedtest", displayName="Speedtest", cmd="speedtest", description="<a href=\"https://www.speedtest.net/apps/cli\">Speedtest</a> by Ookla is the official command line client for testing the speed and performance of your internet connection.</br></br>You may only use this Speedtest software and information generated " +
        "from it for personal, non-commercial use, through a command line " +
        "interface on a personal computer. Your use of this software is subject " +
        "to the End User License Agreement, Terms of Use and Privacy Policy at " +
        "these URLs:</br>" +
        "</br>" +
        "<a href=\"https://www.speedtest.net/about/eula\">EULA</a></br>" +
        "<a href=\"https://www.speedtest.net/about/terms\">Terms</a></br>" +
        "<a href=\"https://www.speedtest.net/about/privacy\">Privacy</a></br>" +
        "</br>" +
        "Ookla collects certain data through Speedtest that may be considered" +
        "personally identifiable, such as your IP address, unique device" +
        "identifiers or location. Ookla believes it has a legitimate interest" +
        "to share this data with internet providers, hardware manufacturers and" +
        "industry regulators to help them understand and create a better and" +
        "faster internet. For further information including how the data may be" +
        "shared, where the data may be transferred and Ookla's contact details," +
        "please see our Privacy Policy at:</br>" +
        "</br>" +
        "<a href=\"http://www.speedtest.net/privacy\">Privacy</a>")
public class Speedtest implements Payload {
    @FixedParam(param = "--progress", paramType = ParameterType.EQUALS)
    private String privileged = "no";

    @FixedParam(param = "--accept-license", paramType = ParameterType.ONLY_PARAM)
    private Boolean acceptLicense = true;

    @FixedParam(param = "--accept-gdpr", paramType = ParameterType.ONLY_PARAM)
    private Boolean acceptGdpr = true;

    @NumberParam(displayName = "Precision", param = "--precision", description = "Number of decimals to use", min = 0, max = 8, paramType = ParameterType.EQUALS)
    private Integer precision = 2;

    @EnumParam(displayName = "Unit", param = "--unit", description = "Output unit for displaying speeds (Note: this is only applicable for ‘human-readable’ output format and the default unit is Mbps)", paramType = ParameterType.EQUALS)
    private Unit unit;

    @BooleanParam(displayName ="Selection details", param="--selection-details", description="Show server selection details", paramType = ParameterType.EQUALS)
    private Boolean selectionDetails;

    @Override
    public String getCacheString() {
        return "";
    }
}
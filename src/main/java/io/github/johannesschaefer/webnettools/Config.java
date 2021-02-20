package io.github.johannesschaefer.webnettools;

import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.metadata.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.reflections.Reflections;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/config")
public class Config {
    private static final Logger LOG = Logger.getLogger(Config.class);

    @ConfigProperty(name = "AVAILABLE_TOOLS", defaultValue = "testssl,ping,traceroute,nmap,demo")
    List<String> availableTools;

    @Inject
    Reflections reflections;

    @GET
    @Produces
    @Singleton
    public ToolConfiguration config() throws IOException {
        var tools = reflections.getTypesAnnotatedWith(Tool.class);
        return new ToolConfiguration(availableTools, tools.stream().map(this::toToolMD).collect(Collectors.toList()));
    }

    boolean checkTypes(Field field) {
        return field.getDeclaredAnnotation(BooleanParam.class) != null ||
               field.getDeclaredAnnotation(NumberParam.class) != null ||
               field.getDeclaredAnnotation(StringParam.class) != null;
    }

    private ToolMD toToolMD(Class<?> toolClass) {
        Tool toolAnno = toolClass.getAnnotation(Tool.class);

        var options=Arrays.stream(toolClass.getDeclaredFields())
                .filter(this::checkTypes)
                .map(this::toOptionMD)
                .collect(Collectors.toList());

        var mainParam=Arrays.stream(toolClass.getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(MainParameter.class) != null)
                .map(this::toOptionMD).findAny();

        return new ToolMD(toolAnno.name(), toolAnno.displayName(), toolAnno.description(), mainParam.get(), options);
    }

    private OptionMD toOptionMD(Field paramField) {
        BooleanParam paramBooleanAnno = paramField.getDeclaredAnnotation(BooleanParam.class);
        if (paramBooleanAnno != null) {
            return new BooleanOptionMD(paramField.getName(), paramBooleanAnno.displayName(), "boolean", paramBooleanAnno.description(), paramBooleanAnno.defaultValue(), paramBooleanAnno.labelTrue(), paramBooleanAnno.labelFalse());
        }

        StringParam paramStringAnno = paramField.getDeclaredAnnotation(StringParam.class);
        if (paramStringAnno != null) {
            return new StringOptionMD(paramField.getName(), paramStringAnno.displayName(), "string", paramStringAnno.description(), paramStringAnno.defaultValue(), paramStringAnno.minLength(), paramStringAnno.maxLength());
        }

        NumberParam paramNumberAnno = paramField.getDeclaredAnnotation(NumberParam.class);
        if (paramNumberAnno != null) {
            return new NumberOptionMD(paramField.getName(), paramNumberAnno.displayName(), "number", paramNumberAnno.description(), paramNumberAnno.min(), paramNumberAnno.max(), paramNumberAnno.step(), paramNumberAnno.defaultValue());
        }

        MainParameter mainAnno = paramField.getDeclaredAnnotation(MainParameter.class);
        if (mainAnno != null) {
            return new StringOptionMD(paramField.getName(), mainAnno.displayName(), paramField.getType().toString(), mainAnno.description(), mainAnno.defaultValue(), mainAnno.minLength(), mainAnno.maxLength());
        }
        throw new RuntimeException("no matching annotation");
    }
}
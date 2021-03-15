package io.github.johannesschaefer.webnettools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.metadata.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/config")
public class Config {
    @Inject
    Reflections reflections;

    @Inject
    @AvailableToolsQualifier
    List<String> availableTools;

    @ConfigProperty(name = "INTRO_TEXT")
    Optional<String> introText;

    @GET
    public ToolConfiguration config() throws IOException {
        var tools = reflections.getTypesAnnotatedWith(Tool.class);
        return new ToolConfiguration(tools.
                                        stream().
                                        filter(this::filterTools).
                                        map(this::toToolMD).
                                        collect(Collectors.toList()), introText.orElse(""));
    }

    private boolean filterTools(Class<?> aToolClass) {
        return availableTools.contains(aToolClass.getDeclaredAnnotation(Tool.class).name());
    }

    boolean checkTypes(Field field) {
        return field.getDeclaredAnnotation(BooleanParam.class) != null ||
               field.getDeclaredAnnotation(NumberParam.class) != null ||
               field.getDeclaredAnnotation(StringParam.class) != null ||
               field.getDeclaredAnnotation(FileParam.class) != null ||
               field.getDeclaredAnnotation(EnumParam.class) != null;
    }

    private ToolMD toToolMD(Class<?> toolClass) {
        Tool toolAnno = toolClass.getAnnotation(Tool.class);

        Object sampleToolConfig;
        try {
            sampleToolConfig = toolClass.getConstructor().newInstance();
        }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        var options=Arrays.stream(toolClass.getDeclaredFields())
                .filter(this::checkTypes)
                .map(paramField -> toOptionMD(paramField, sampleToolConfig))
                .collect(Collectors.toList());

        var mainParam=Arrays.stream(toolClass.getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(MainParameter.class) != null)
                .map(paramField -> toOptionMD(paramField, sampleToolConfig)).findAny();

        List<GroupMD> groups = getGroups(toolClass);

        return new ToolMD(toolAnno.name(), toolAnno.displayName(), toolAnno.description(), mainParam.orElseGet(()->null), options, groups);
    }

    private List<GroupMD> getGroups(Class<?> toolClass) {
        Group[] definedGroups = {};
        if (toolClass.getDeclaredAnnotation(GroupValues.class) != null) {
            definedGroups = toolClass.getDeclaredAnnotation(GroupValues.class).value();
        }
        List<GroupMD> groups = Lists.newArrayList();

        for (Group dg: definedGroups) {
            groups.add(new GroupMD(dg.name(), dg.description()));
        }

        for (Field f: toolClass.getDeclaredFields()) {
            for (var anno : f.getDeclaredAnnotations()) {
                try {
                    String g = (String) anno.getClass().getMethod("group").invoke(anno);
                    if(groups.stream().noneMatch(a -> a.getName().equals(g))) {
                        groups.add(new GroupMD(g, ""));
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    continue;
                }
            }
        }

        return groups;
    }

    private OptionMD toOptionMD(Field paramField, Object sampleToolConfig) {
        {
            BooleanParam paramBooleanAnno = paramField.getDeclaredAnnotation(BooleanParam.class);
            if (paramBooleanAnno != null) {
                return BooleanOptionMD.builder().
                        name(paramField.getName()).
                        displayName(paramBooleanAnno.displayName()).
                        type("boolean").
                        description(paramBooleanAnno.description()).
                        group(paramBooleanAnno.group()).
                        defaultValue(getDefaultValue(paramField, sampleToolConfig)).
                        labelTrue(paramBooleanAnno.labelTrue()).
                        labelFalse(paramBooleanAnno.labelFalse()).build();
            }
        }

        {
            StringParam paramStringAnno = paramField.getDeclaredAnnotation(StringParam.class);
            if (paramStringAnno != null) {
                return StringOptionMD.builder().
                        name(paramField.getName()).
                        displayName(paramStringAnno.displayName()).
                        type("string").
                        description(paramStringAnno.description()).
                        group(paramStringAnno.group()).
                        defaultValue(getDefaultValue(paramField, sampleToolConfig)).
                        minlength(paramStringAnno.minLength()).
                        maxlength(paramStringAnno.maxLength()).build();
            }
        }

        {
            NumberParam paramNumberAnno = paramField.getDeclaredAnnotation(NumberParam.class);
            if (paramNumberAnno != null) {
                return NumberOptionMD.builder().
                        name(paramField.getName()).
                        displayName(paramNumberAnno.displayName()).
                        type("number").
                        description(paramNumberAnno.description()).
                        group(paramNumberAnno.group()).
                        defaultValue(getDefaultValue(paramField, sampleToolConfig)).
                        min(paramNumberAnno.min()).
                        max(paramNumberAnno.max()).
                        step(paramNumberAnno.step()).build();
            }
        }

        {
            FileParam paramFileAnno = paramField.getDeclaredAnnotation(FileParam.class);
            if (paramFileAnno != null) {
                return FileOptionMD.builder().
                        name(paramField.getName()).
                        displayName(paramFileAnno.displayName()).
                        type("file").
                        maxSize(paramFileAnno.maxSize()).
                        accept(paramFileAnno.accept()).
                        description(paramFileAnno.description()).
                        group(paramFileAnno.group()).build();
            }
        }

        {
            EnumParam paramEnumAnno = paramField.getDeclaredAnnotation(EnumParam.class);
            if (paramEnumAnno != null) {
                return EnumOptionMD.builder().
                        name(paramField.getName()).
                        displayName(paramEnumAnno.displayName()).
                        type("enum").
                        description(paramEnumAnno.description()).
                        group(paramEnumAnno.group()).
                        defaultValue(getDefaultValueEnum(paramField, sampleToolConfig)).
                        values(getEnumValues((Class<? extends Enum>) paramField.getType())).build();
            }
        }

        {
            MainParameter mainAnno = paramField.getDeclaredAnnotation(MainParameter.class);
            if (mainAnno != null) {
                return StringOptionMD.builder().
                        name(paramField.getName()).
                        displayName(mainAnno.displayName()).
                        type("string").
                        description(mainAnno.description()).
                        defaultValue(getDefaultValue(paramField, sampleToolConfig)).
                        minlength(mainAnno.minLength()).
                        maxlength(mainAnno.maxLength()).build();
            }
        }
        throw new RuntimeException("no matching annotation");
    }

    private <T> T getDefaultValue(Field field, Object sample) {
        try {
            field.setAccessible(true);
            return (T)field.get(sample);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDefaultValueEnum(Field field, Object sample) {
        try {
            field.setAccessible(true);
            Object defaultValue = field.get(sample);
            if (defaultValue == null) {
                return null;
            }
            return (String)field.getType().getMethod("name").invoke(defaultValue);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getEnumValues(Class<? extends Enum> type) {
        try {
            var values = (Object[])type.getMethod("values").invoke(null);
            Map<String, String> ret = Maps.newHashMap();
            for (Object v : values) {
                ret.put((String) type.getMethod("name").invoke(v), (String) type.getMethod("toString").invoke(v));
            }
            return ret;
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
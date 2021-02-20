package io.github.johannesschaefer.webnettools;

import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.payload.Payload;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/tools/")
public class Tools {
    @Inject
    Logger log;

    @Inject
    ToolConfiguration config;

    @POST
    @Path("{tool}")
    public Response genericTool(@PathParam("tool") String tool, Payload payload) throws IOException, IllegalAccessException {
        if (!config.getAvailableTools().contains(tool)) {
            return Response.serverError().entity(tool+" not in list of available tools").build();
        }

        checkAndCorrectPayload(payload);

        List<String> cmd = Lists.newArrayList();

        cmd.addAll(getCmd(payload));

        for (Field f : payload.getClass().getDeclaredFields()) {
            cmd.addAll(getParam(payload, f));
        }

        cmd.addAll(getMain(payload));

        return getStreamResponse(cmd);
    }

    private ArrayList<String> getMain(Payload payload) throws IllegalAccessException {
        Field mainField = getMainField(payload.getClass());
        mainField.setAccessible(true);
        return Lists.newArrayList(mainField.get(payload).toString());
    }

    private List<String> getCmd(Payload payload) {
        Tool toolAnno = payload.getClass().getDeclaredAnnotation(Tool.class);
        return Lists.newArrayList(toolAnno.cmd());
    }

    private void checkAndCorrectPayload(Payload payload) throws IllegalAccessException {
        for (Field f : payload.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            var value = f.get(payload);
            if (f.getDeclaredAnnotation(MainParameter.class) != null) {
                var anno = f.getDeclaredAnnotation(MainParameter.class);
                if(anno.hasDefaultValue() && value == null) {
                    f.set(payload, anno.defaultValue());
                    continue;
                }
                if (value == null) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is empty");
                }
            }
            if (f.getDeclaredAnnotation(BooleanParam.class) != null) {
                var anno = f.getDeclaredAnnotation(BooleanParam.class);
                if(anno.hasDefaultValue() && value == null) {
                    f.set(payload, anno.defaultValue());
                    continue;
                }
            }
            if (f.getDeclaredAnnotation(StringParam.class) != null) {
                var anno = f.getDeclaredAnnotation(StringParam.class);
                if(anno.hasDefaultValue() && value == null) {
                    f.set(payload, anno.defaultValue());
                    continue;
                }
            }
            if (f.getDeclaredAnnotation(NumberParam.class) != null) {
                var anno = f.getDeclaredAnnotation(NumberParam.class);
                if(anno.hasDefaultValue() && value == null) {
                    f.set(payload, anno.defaultValue());
                    continue;
                }
                if (value == null) {
                    continue;
                }

                double doubleValue;
                if (value instanceof Number) {
                    doubleValue = ((Number) value).doubleValue();
                }
                else if(value instanceof String) {
                    doubleValue = Double.parseDouble((String) value);
                }
                else {
                    throw new RuntimeException("invalid type of " + value);
                }

                if(anno.min() > doubleValue) {
                    f.set(payload, anno.min());
                    continue;
                }
                if(anno.max() < doubleValue) {
                    f.set(payload, anno.max());
                }
            }
        }
    }

    private Collection<String> getParam(Payload payload, Field field) throws IllegalAccessException {
        if (field.getDeclaredAnnotation(ServerParam.class) != null) {
            return getServerParam(field);
        }

        field.setAccessible(true);
        if (field.get(payload) == null) {
            return Lists.newArrayList();
        }
        var value = field.get(payload).toString();
        if (field.getDeclaredAnnotation(BooleanParam.class) != null) {
            return getBooleanParam(value, field);
        }
        if (field.getDeclaredAnnotation(StringParam.class) != null) {
            return getStringParam(value, field);
        }
        if (field.getDeclaredAnnotation(NumberParam.class) != null) {
            return getNumberParam(value, field);
        }
        return Lists.newArrayList();
    }

    private Collection<String> getServerParam(Field field) {
        try
        {
            var anno = field.getDeclaredAnnotation(ServerParam.class);
            var instance = anno.handler().getDeclaredConstructor().newInstance();
            return instance.handle(field);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Can't handle server parameter.", e);
        }
    }

    private Collection<String> getBooleanParam(String value, Field field) {
        BooleanParam anno = field.getDeclaredAnnotation(BooleanParam.class);
        switch(anno.paramType()) {
            case SPACE:
                return Lists.newArrayList(anno.param(), value);
            case EQUALS:
                return Lists.newArrayList(anno.param()+"="+ value);
            case ONLY_PARAM:
                if (Boolean.parseBoolean(value)) {
                    return Lists.newArrayList(anno.param());
                }
                else {
                    return Lists.newArrayList();
                }
            case ONLY_VALUE:
                return Lists.newArrayList(value);
            default:
                throw new RuntimeException("Unsupported paramType " + anno.paramType());
        }
    }

    private Collection<String> getStringParam(String value, Field field) {
        StringParam anno = field.getDeclaredAnnotation(StringParam.class);
        String paramStr = anno.param();
        return buildReturn(anno.paramType(), paramStr, value);
    }

    private Collection<String> buildReturn(ParameterType pType, String paramStr, String value) {
        switch(pType) {
            case SPACE:
                return Lists.newArrayList(paramStr, value);
            case EQUALS:
                return Lists.newArrayList(paramStr+"="+ value);
            case ONLY_VALUE:
                return Lists.newArrayList(value);
            default:
                throw new RuntimeException("Unsupported paramType " + pType);
        }
    }

    private Collection<String> getNumberParam(String value, Field field) {
        NumberParam anno = field.getDeclaredAnnotation(NumberParam.class);
        String paramStr = anno.param();
        return buildReturn(anno.paramType(), paramStr, value);
    }

    private Field getMainField(Class<? extends Payload> aClass) {
        var fields = aClass.getDeclaredFields();
        for (Field f : fields) {
            var mainParam = f.getDeclaredAnnotation(MainParameter.class);
            if (mainParam != null) {
                return f;
            }
        }
        throw new RuntimeException("no main field declared");
    }

    private Response getStreamResponse(List<String> cmd) {
        try
        {
            log.info("command: " + String.join(" ", cmd));
            Process process = new ProcessBuilder().command(cmd).redirectErrorStream(true).start();
            return Response.ok((StreamingOutput) outputStream -> copyStream(process.getInputStream(), outputStream)).build();
        }
        catch(Exception e) {
            log.error(e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    void copyStream(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
            target.flush();
        }
    }
}
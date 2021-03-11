package io.github.johannesschaefer.webnettools;

import com.google.common.collect.Lists;
import io.github.johannesschaefer.webnettools.annotation.*;
import io.github.johannesschaefer.webnettools.payload.Payload;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Path("/tools/")
public class Tools {
    List<Class> STANDARD_PARAMETER_CLASSES = Lists.newArrayList(BooleanParam.class, StringParam.class, NumberParam.class, EnumParam.class, FixedParam.class);

    @Inject
    Logger log;

    @Inject
    @AvailableToolsQualifier
    List<String> availableTools;

    @POST
    @Path("{tool}")
    public Response genericTool(@PathParam("tool") String tool, Payload payload) {
        if (!availableTools.contains(tool)) {
            return Response.serverError().entity(tool+" not in list of available tools").build();
        }

        try {
            checkAndCorrectPayload(payload);

            List<String> cmd = Lists.newArrayList();

            cmd.addAll(getCmd(payload));

            for (Field f : payload.getClass().getDeclaredFields()) {
                cmd.addAll(getParam(payload, f));
            }

            cmd.addAll(getMain(payload));

            return getStreamResponse(cmd);
        }
        catch (Exception e) {
                log.error(e);
                return Response.serverError().entity(e.getMessage()).build();
        }
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

    private void checkAndCorrectPayload(Payload payload) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        var sample = payload.getClass().getConstructor().newInstance();
        for (Field f : payload.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            var defaultValue = f.get(sample);
            var value = f.get(payload);
            if (f.getDeclaredAnnotation(MainParameter.class) != null) {
                var anno = f.getDeclaredAnnotation(MainParameter.class);
                if(defaultValue != null && value == null) {
                    f.set(payload, defaultValue);
                    continue;
                }
                if (value == null) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is empty");
                }
            }
            if (f.getDeclaredAnnotation(BooleanParam.class) != null) {
                if(defaultValue != null && value == null) {
                    f.set(payload, defaultValue);
                    continue;
                }
            }
            if (f.getDeclaredAnnotation(StringParam.class) != null) {
                var anno = f.getDeclaredAnnotation(StringParam.class);
                if(defaultValue != null && value == null) {
                    f.set(payload, defaultValue);
                    continue;
                }
                if (value == null) {
                    continue;
                }
                if (!(value instanceof String)) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is not of type String");
                }
                if (((String)value).length() < anno.minLength()) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is shorter than needed (min length: " + anno.minLength() + ")." );
                }
                if (((String)value).length() > anno.maxLength()) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is longer than needed (max length: " + anno.maxLength() + ")." );
                }
            }
            if (f.getDeclaredAnnotation(FileParam.class) != null) {
                if (value == null) {
                    continue;
                }
                var anno = f.getDeclaredAnnotation(FileParam.class);
                if(((String)value).length() /1.33 > anno.maxSize()) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is larger than allowed (max size: " + anno.maxSize() + ")." );
                }

                if(((String)value).indexOf("base64,") < 0) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is not a valid base64 encoded file." );
                }
                if(((String)value).indexOf("filename:") < 0) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is not containing a filename." );
                }
                if(((String)value).indexOf(";data:") < 0) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is not containing a filename." );
                }

                continue;
            }
            if (f.getDeclaredAnnotation(NumberParam.class) != null) {
                var anno = f.getDeclaredAnnotation(NumberParam.class);
                if(defaultValue != null && value == null) {
                    f.set(payload, defaultValue);
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
                    throw new RuntimeException("Invalid type of " + value);
                }

                if(anno.min() > doubleValue) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is out of range (min: " + anno.min() + ")");
                }
                if(anno.max() < doubleValue) {
                    throw new RuntimeException("Parameter " + anno.displayName() + " is out of range (max: " + anno.max() + ")");
                }
            }
        }
    }

    private Collection<String> getParam(Payload payload, Field field) throws IllegalAccessException {
        if (field.getDeclaredAnnotation(ServerParam.class) != null) {
            return getServerParam(field, payload);
        }

        field.setAccessible(true);
        if (field.get(payload) == null) {
            return Lists.newArrayList();
        }
        var value = field.get(payload).toString();
        if (field.getDeclaredAnnotation(FileParam.class) != null) {
            return getFileParam(value, field);
        }

        var anno = Arrays.stream(field.getDeclaredAnnotations()).filter(f->STANDARD_PARAMETER_CLASSES.contains(f.annotationType())).findFirst();
        if (anno.isPresent()) {
            return getParam(value, anno.get());
        }
        return Lists.newArrayList();
    }

    private Collection<String> getFileParam(String value, Field field) {
        int posBase64 = value.indexOf("base64,");
        int posFilename = value.indexOf("filename:");
        int posData = value.indexOf(";data:");
        String filename = value.substring(posFilename + 9, posData);
        var content = DatatypeConverter.parseBase64Binary(value.substring(posBase64 + 7));
        try {
            var tmpFile = File.createTempFile("webnettools", filename);
            FileUtils.writeByteArrayToFile(tmpFile, content);
            tmpFile.deleteOnExit();

            return buildReturn(field.getDeclaredAnnotation(FileParam.class).paramType(), field.getDeclaredAnnotation(FileParam.class).param(), tmpFile.getPath());
        }
        catch(IOException e ) {
            throw new RuntimeException(e);
        }
    }

    private Collection<String> getServerParam(Field field, Payload payload) {
        return CDI.current().select(field.getDeclaredAnnotation(ServerParam.class).handler()).get().handle(field, payload);
    }

    private Collection<String> getParam(String value, Annotation anno) {
        try {
            String paramStr = (String) anno.getClass().getMethod("param").invoke(anno);
            ParameterType paramType = (ParameterType) anno.getClass().getMethod("paramType").invoke(anno);
            return buildReturn(paramType, paramStr, value);
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<String> buildReturn(ParameterType pType, String paramStr, String value) {
        switch(pType) {
            case SPACE:
                return Lists.newArrayList(paramStr, value);
            case EQUALS:
                return Lists.newArrayList(paramStr+"="+ value);
            case ONLY_VALUE:
                return Lists.newArrayList(value);
            case ONLY_PARAM:
                if (Boolean.parseBoolean(value)) {
                    return Lists.newArrayList(paramStr);
                }
                else {
                    return Lists.newArrayList();
                }
            default:
                throw new RuntimeException("Unsupported paramType " + pType);
        }
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

    private Response getStreamResponse(List<String> cmd) throws IOException {
        log.info("command: " + String.join(" ", cmd));
        Process process = new ProcessBuilder().command(cmd).redirectErrorStream(true).start();
        return Response.ok((StreamingOutput) outputStream -> copyStream(process.getInputStream(), outputStream)).build();
    }

    private void copyStream(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
            target.flush();
        }
    }
}
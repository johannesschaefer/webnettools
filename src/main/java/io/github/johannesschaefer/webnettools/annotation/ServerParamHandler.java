package io.github.johannesschaefer.webnettools.annotation;

import io.github.johannesschaefer.webnettools.payload.Payload;

import java.lang.reflect.Field;
import java.util.Collection;

public interface ServerParamHandler {
    Collection<String> handle(Field field, Payload payload);
}
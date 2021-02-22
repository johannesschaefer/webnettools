package io.github.johannesschaefer.webnettools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumParam {
    String displayName();

    String description();

    String param();

    String group() default "Options";

    ParameterType paramType() default ParameterType.SPACE;
}
package io.github.johannesschaefer.webnettools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanParam {
    String displayName();

    String description();

    String param();

    boolean hasDefaultValue() default false;

    boolean defaultValue() default false;

    String labelTrue() default "True";

    String labelFalse() default "False";

    ParameterType paramType() default ParameterType.SPACE;
}
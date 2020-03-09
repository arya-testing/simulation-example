package dev.testment.core.simulation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String name() default "";
    String defaultValue() default "";
    boolean required() default true;
    String description() default "";
    String[] examples() default "";
}

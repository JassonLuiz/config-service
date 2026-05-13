package io.github.clientlibrary.client_library.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigValue {
    String key();
    String defaultValue() default "";
}

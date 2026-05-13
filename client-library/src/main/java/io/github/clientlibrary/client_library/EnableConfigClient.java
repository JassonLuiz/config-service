package io.github.clientlibrary.client_library;

import io.github.clientlibrary.client_library.config.ConfigClientAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConfigClientAutoConfiguration.class)
public @interface EnableConfigClient {
}

package io.github.clientlibrary.client_library.config;

import io.github.clientlibrary.client_library.annotation.ConfigValue;
import io.github.clientlibrary.client_library.service.cache.ConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConfigValueInjector implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(ConfigValueInjector.class);

    private final ConfigClient client;
    private final Environment env;
    private final Set<Object> beansWithConfig = ConcurrentHashMap.newKeySet();

    public ConfigValueInjector(ConfigClient client, Environment env) {
        this.client = client;
        this.env = env;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (hasConfigValueFields(bean.getClass())) {
            injectValue(bean);
            beansWithConfig.add(bean);
            log.debug("Processed config injection for bean: {}", beanName);
        }
        return bean;
    }

    public void refreshAll(){
        log.info("Refreshing config values for {} beans", beansWithConfig.size());
        beansWithConfig.forEach(bean -> {
            try {
                injectValue(bean);
            } catch (Exception e) {
                log.error("Error refreshing config for bean {}: {}", bean.getClass().getSimpleName(), e.getMessage());
            }
        });
    }

    private boolean hasConfigValueFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(field -> field.isAnnotationPresent(ConfigValue.class));
    }

    private void injectValue(Object bean) {
        String namespace = env.getProperty("config.namespace");
        String environment = env.getProperty("config.environment");

        if (!StringUtils.hasText(namespace) || !StringUtils.hasText(environment)) {
            log.warn("Missing config.namespace or config.environment properties");
            return;
        }

        for (Field field : bean.getClass().getDeclaredFields()) {
            ConfigValue annotation = field.getAnnotation(ConfigValue.class);
            if (annotation == null) continue;

            try {
                injectFieldValue(bean, field, annotation, namespace, environment);
            } catch (Exception ignored) {}
        }
    }

    private void injectFieldValue(Object bean, Field field, ConfigValue annotation, String namespace, String environment) {
        String configValue = client.getString(namespace, environment, annotation.key());
        String finalValue = (configValue != null) ? configValue : annotation.defaultValue();

        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        try {
            if (fieldType == String.class) {
                field.set(bean, finalValue);
            }

            log.trace("Injected config {}:{}:{} = '{}' into {}.{}",
                    namespace, environment, annotation.key(), finalValue,
                    bean.getClass().getSimpleName(), field.getName());

        } catch (Exception e) {
            log.error("Invalid number format '{}' for field {} of type {}",
                    finalValue, field.getName(), fieldType.getSimpleName());
            throw new IllegalArgumentException("Invalid config value format", e);
        }
    }

    public void clearBeans() {
        beansWithConfig.clear();
    }

    public int getBeanCount() {
        return beansWithConfig.size();
    }

}

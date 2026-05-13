package io.github.clientlibrary.client_library.service.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicConfigHolder {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public void put(String ns, String env, String key, String value) {
        cache.put(buildKey(ns, env, key), value);
    }

    public void remove(String ns, String env, String key) {
        cache.remove(buildKey(ns, env, key));
    }

    public String get(String ns, String env, String key) {
        return cache.get(buildKey(ns, env, key));
    }

    private String buildKey(String ns, String env, String key) {
        return "%s:%s:%s".formatted(ns, env, key);
    }

}

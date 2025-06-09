package io.github.configservice.config_service.exception;

public class ConfigNotFoundException extends RuntimeException{
    public ConfigNotFoundException(String message) {
        super(message);
    }
}

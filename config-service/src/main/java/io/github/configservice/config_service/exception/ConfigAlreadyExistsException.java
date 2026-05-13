package io.github.configservice.config_service.exception;

public class ConfigAlreadyExistsException extends RuntimeException{

    public ConfigAlreadyExistsException(String message){
        super(message);
    }
}

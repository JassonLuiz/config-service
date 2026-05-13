package io.github.configservice.config_service.exception;

public class EnvironmentNotFoundException extends RuntimeException{
    public EnvironmentNotFoundException(String message){
        super(message);
    }
}

package io.github.configservice.config_service.constants;

public final class DLQStatus {

    private DLQStatus(){

    }

    public static final String PENDING = "PENDING";
    public static final String RETRYING = "RETRYING";
    public static final String FAILED = "FAILED";
    public static final String RESOLVED = "RESOLVED";
}

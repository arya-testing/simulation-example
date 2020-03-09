package dev.testment.core.logger;

public enum LogLevel {
    OFF("OFF"),
    ERROR("ERROR"),
    WARNING("WARNING"),
    INFO("INFO"),
    DEBUG("DEBUG"),
    VERBOSE("VERBOSE");

    private String value;

    LogLevel(String value) {
        this.value = value;
    }
}

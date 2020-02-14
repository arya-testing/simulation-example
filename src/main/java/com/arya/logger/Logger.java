package com.arya.logger;

public interface Logger {
    void setLevel(LogLevel level);
    void error(String format, String... args);
    void warning(String format, String... args);
    void info(String format, String... args);
    void debug(String format, String... args);
    void verbose(String format, String... args);
}

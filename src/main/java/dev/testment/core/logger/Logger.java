package dev.testment.core.logger;

public interface Logger {
    void setLevel(LogLevel level);
    LogLevel getLevel();
    void error(String format, String... args);
    void warning(String format, String... args);
    void info(String format, String... args);
    void debug(String format, String... args);
    void verbose(String format, String... args);
}

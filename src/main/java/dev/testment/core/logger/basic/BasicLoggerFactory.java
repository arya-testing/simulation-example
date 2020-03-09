package dev.testment.core.logger.basic;

import dev.testment.core.logger.LogLevel;
import dev.testment.core.util.Validation;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class BasicLoggerFactory {

    private LogLevel logLevel = LogLevel.INFO;
    private List<BasicLogger> loggers = new ArrayList<>();

    public <T> BasicLogger getLogger(Class<T> clazz) {
        return getLogger(clazz, System.out);
    }

    public <T> BasicLogger getLogger(Class<T> clazz, PrintStream printStream) {
        Validation.notNull("clazz", clazz);
        BasicLogger logger = new BasicLogger(clazz.getName(), printStream);
        logger.setLevel(logLevel);
        loggers.add(logger);
        return logger;
    }

    public void setGlobalLevel(LogLevel logLevel) {
        Validation.notNull("logLevel", logLevel);
        this.logLevel = logLevel;
        for(BasicLogger logger : loggers) {
            logger.setLevel(logLevel);
        }
    }

}

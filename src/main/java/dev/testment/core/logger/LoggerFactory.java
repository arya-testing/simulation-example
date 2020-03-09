package dev.testment.core.logger;

import dev.testment.core.logger.basic.BasicLoggerFactory;

public class LoggerFactory {

    private static BasicLoggerFactory basicLoggerFactory = new BasicLoggerFactory();

    public static <T> Logger getLogger(Class<T> clazz) {
        return basicLoggerFactory.getLogger(clazz);
    }

    public static void setGlobalLevel(LogLevel logLevel) {
        basicLoggerFactory.setGlobalLevel(logLevel);
    }

}

package dev.testment.core.logger;

import dev.testment.core.cli.Args;

public class LoggerConfigurator {

    public void configure(String[] args) {
        LogLevel logLevel = LogLevel.valueOf(Args.getValue(args, "log-level", LogLevel.INFO.name()).toUpperCase());
        LoggerFactory.setGlobalLevel(logLevel);
    }

}

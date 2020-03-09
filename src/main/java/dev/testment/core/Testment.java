package dev.testment.core;

import dev.testment.core.application.TestmentApplicationRunner;
import dev.testment.core.logger.Logger;
import dev.testment.core.logger.LoggerFactory;

public class Testment {

    private static final Logger logger = LoggerFactory.getLogger(Testment.class);

    private Testment() { }

    public static void runApplication(Class<?> clazz, String[] args) {
        TestmentApplicationRunner testmentApplicationRunner = new TestmentApplicationRunner();
        testmentApplicationRunner.run(clazz, args);
    }

}
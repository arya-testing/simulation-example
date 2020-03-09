package dev.testment.core.logger;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerFactoryTests {

    @Test
    public void testGetLogger() {
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTests.class);
        assertThat(logger).isNotNull();
    }

    @Test
    public void testSetGlobalLevel() {
        LoggerFactory.setGlobalLevel(LogLevel.INFO);
    }

}

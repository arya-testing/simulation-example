package dev.testment.core.logger.basic;

import dev.testment.core.logger.LogLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BasicLoggerFactoryTests {

    @InjectMocks
    private BasicLoggerFactory basicLoggerFactory;

    @Test
    public void testGetLogger() {
        BasicLogger logger = this.basicLoggerFactory.getLogger(BasicLoggerFactoryTests.class);
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo(BasicLoggerFactoryTests.class.getName());
        assertThat(logger.getPrintStream()).isEqualTo(System.out);
        assertThat(logger.getLevel()).isEqualTo(LogLevel.INFO);
    }

    @Test
    public void testGetLoggerWithPrintStream() {
        PrintStream printStreamMock = mock(PrintStream.class);
        BasicLogger logger = this.basicLoggerFactory.getLogger(BasicLoggerFactoryTests.class, printStreamMock);
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo(BasicLoggerFactoryTests.class.getName());
        assertThat(logger.getPrintStream()).isEqualTo(printStreamMock);
        assertThat(logger.getLevel()).isEqualTo(LogLevel.INFO);
    }

    @Test
    public void testSetGlobalLevelBeforeGetLogger() {
        this.basicLoggerFactory.setGlobalLevel(LogLevel.VERBOSE);
        BasicLogger logger1 = this.basicLoggerFactory.getLogger(BasicLoggerFactoryTests.class);
        BasicLogger logger2 = this.basicLoggerFactory.getLogger(BasicLoggerFactoryTests.class);
        assertThat(logger1.getLevel()).isEqualTo(LogLevel.VERBOSE);
        assertThat(logger2.getLevel()).isEqualTo(LogLevel.VERBOSE);
    }

    @Test
    public void testSetGlobalLevelAfterGetLogger() {
        BasicLogger logger1 = this.basicLoggerFactory.getLogger(BasicLoggerFactoryTests.class);
        BasicLogger logger2 = this.basicLoggerFactory.getLogger(BasicLoggerFactoryTests.class);
        this.basicLoggerFactory.setGlobalLevel(LogLevel.OFF);
        assertThat(logger1.getLevel()).isEqualTo(LogLevel.OFF);
        assertThat(logger2.getLevel()).isEqualTo(LogLevel.OFF);
    }

}

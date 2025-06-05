package com.sam.assignment;

import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

public class BootStrapApplicationTest {

    @Test
    void testMainWithNoArguments() throws IllegalAccessException {
        Logger mockLogger = mock(Logger.class);
        PowerMockito.field(BootStrapApplication.class, "log").set(null, mockLogger);
        BootStrapApplication.main(new String[]{});
        verify(mockLogger).error(contains("Please provide below mandatory application arguments"));
    }

    @Test
    void testMainWithInvalidNumberFormat() throws IllegalAccessException {
        Logger mockLogger = mock(Logger.class);
        PowerMockito.field(BootStrapApplication.class, "log").set(null, mockLogger);
        String[] args = {"file.csv", "notANumber", "10", "5"};
        BootStrapApplication.main(args);
        verify(mockLogger).error(contains("Error building organizational hierarchy. error-message : Character n is neither a decimal digit number, decimal point, nor \"e\" notation exponential mark."));
    }

    @Test
    void testMainWithValidArgumentsButFileNotFound() throws IllegalAccessException {
        Logger mockLogger = mock(Logger.class);
        PowerMockito.field(BootStrapApplication.class, "log").set(null, mockLogger);
        String[] args = {"nonexistent.csv", "10", "20", "5"};
        BootStrapApplication.main(args);
        verify(mockLogger).error(contains("Error reading employee details from file. error-message : nonexistent.csv (The system cannot find the file specified)"));
    }

    @Test
    void testMainWithValidArguments() throws IllegalAccessException {
        Logger mockLogger = mock(Logger.class);
        PowerMockito.field(BootStrapApplication.class, "log").set(null, mockLogger);
        URL resourceUrl = getClass().getResource("/employee-details.csv");
        assert resourceUrl != null;
        String[] args = {resourceUrl.getPath(), "20", "40", "2"};
        BootStrapApplication.main(args);
    }
}
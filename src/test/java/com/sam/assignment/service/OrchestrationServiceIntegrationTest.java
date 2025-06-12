package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Response;
import com.sam.assignment.util.ValidationUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class OrchestrationServiceIntegrationTest {

    private final ValidationUtil validationUtil;
    private final CsvFileReader csvFileReader;
    private final OrgHierarchy<Employee> orgHierarchy;
    private final OrchestrationService orchestrationService;

    public OrchestrationServiceIntegrationTest() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        validationUtil = new ValidationUtil(validator);
        orgHierarchy = new OrgHierarchyImpl<>();
        csvFileReader = new CsvFileReaderImpl();
        orchestrationService = new OrchestrationService(
                validationUtil,
                csvFileReader,
                orgHierarchy
        );
    }

    @Test
    void testDoOrchestration_Success_20_40_2() throws IOException {
        URL resourceUrl = getClass().getResource("/employee-details.csv");
        assert resourceUrl != null;
        String[] args = {resourceUrl.getPath(), "20", "40", "2"};

        // Act
        Response<Employee> response = orchestrationService.doOrchestration(args);

        // Assert
        assertEquals("0000000123", response.getCeo().getId());
        assertEquals(1, response.getLeastPaidManagers().size());
        assertEquals(1, response.getOverPaidManagers().size());
        assertEquals(1, response.getMaxReportingLineLengthEmployees().size());
        assertEquals("0000000124", response.getLeastPaidManagers().get(0).getId());
        assertEquals("0000000300", response.getOverPaidManagers().get(0).getId());
        assertEquals("0000000305", response.getMaxReportingLineLengthEmployees().get(0).getId());
    }

    @Test
    void testDoOrchestration_Success_20_50_4() throws IOException {
        URL resourceUrl = getClass().getResource("/employee-details.csv");
        assert resourceUrl != null;
        String[] args = {resourceUrl.getPath(), "20", "50", "4"};

        // Act
        Response<Employee> response = orchestrationService.doOrchestration(args);

        // Assert
        assertEquals("0000000123", response.getCeo().getId());
        assertEquals(1, response.getLeastPaidManagers().size());
        assertEquals(0, response.getOverPaidManagers().size());
        assertEquals(0, response.getMaxReportingLineLengthEmployees().size());
        assertEquals("0000000124", response.getLeastPaidManagers().get(0).getId());
    }

    @Test
    void testDoOrchestration_Failure_InvalidPath() {
        String[] args = {"wrong.csv", "20", "50", "4"};

        // Act
        FileNotFoundException ffe = assertThrows(FileNotFoundException.class,
                () -> orchestrationService.doOrchestration(args));

        // Assert
        assertNotNull(ffe);
        assertNotNull(ffe.getMessage());
        assertEquals("wrong.csv (The system cannot find the file specified)", ffe.getMessage());
    }

    @Test
    void testDoOrchestration_Failure_NoArguments() {
        String[] args = {};

        // Act
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> orchestrationService.doOrchestration(args));

        // Assert
        assertNotNull(iae);
        assertNotNull(iae.getMessage());
        assertEquals("Application arguments cannot be null or empty", iae.getMessage());
    }

    @Test
    void testDoOrchestration_Failure_InvalidArguments_1() {
        URL resourceUrl = getClass().getResource("/employee-details.csv");
        assert resourceUrl != null;
        String[] args = {resourceUrl.getPath(), "ABC", "50", "4"};

        // Act
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> orchestrationService.doOrchestration(args));

        // Assert
        assertNotNull(iae);
        assertNotNull(iae.getMessage());
        assertEquals("For input string: \"ABC\"", iae.getMessage());
    }

    @Test
    void testDoOrchestration_Failure_InvalidArguments_2() {
        URL resourceUrl = getClass().getResource("/employee-details.csv");
        assert resourceUrl != null;
        String[] args = {resourceUrl.getPath(), "20", "XYZ", "4"};

        // Act
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> orchestrationService.doOrchestration(args));

        // Assert
        assertNotNull(iae);
        assertNotNull(iae.getMessage());
        assertEquals("For input string: \"XYZ\"", iae.getMessage());
    }

    @Test
    void testDoOrchestration_Failure_InvalidArguments_3() {
        URL resourceUrl = getClass().getResource("/employee-details.csv");
        assert resourceUrl != null;
        String[] args = {resourceUrl.getPath(), "20", "40", "PQR"};

        // Act
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> orchestrationService.doOrchestration(args));

        // Assert
        assertNotNull(iae);
        assertNotNull(iae.getMessage());
        assertEquals("For input string: \"PQR\"", iae.getMessage());
    }

}
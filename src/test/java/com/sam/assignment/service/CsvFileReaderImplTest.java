package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.util.ValidationUtil;
import org.junit.jupiter.api.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvFileReaderImplTest {

    private CsvFileReaderImpl csvFileReader;
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        csvFileReader = new CsvFileReaderImpl();
        validationUtil = mock(ValidationUtil.class);
    }

    @Test
    void testReadEmployeeDetails_ReturnsEmployees() throws IOException {
        // Create temp CSV file
        File tempFile = File.createTempFile("employees", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,firstName,lastName,salary,managerId\n");
            writer.write("0000000001,Alice,Smith,2000,\n");
            writer.write("0000000002,Bob,Jones,1500,1\n");
        }

        Parameter param = new Parameter(tempFile.getAbsolutePath(), BigDecimal.ZERO, BigDecimal.ZERO, 0);

        Map<String, Employee> employees = csvFileReader.readEmployeeDetails(param, validationUtil);

        assertEquals(2, employees.size());
        assertTrue(employees.containsKey("0000000001"));
        assertTrue(employees.containsKey("0000000002"));
        assertEquals(new BigDecimal("2000.00"), employees.get("0000000001").getSalary());
        assertEquals("Alice", employees.get("0000000001").getFirstName());
        assertEquals("1", employees.get("0000000002").getManagerId());

        verify(validationUtil, times(2)).validate(any(Employee.class));
        Files.deleteIfExists(tempFile.toPath());
    }
}
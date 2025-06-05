package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import jakarta.validation.Validator;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvFileReaderImplTest {

    private CsvFileReaderImpl csvFileReader;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Mockito.mock(Validator.class);
        csvFileReader = new CsvFileReaderImpl(validator);
    }

    @Test
    void testReadEmployeeDetails_ReturnsEmployees() throws IOException {
        // Create temp CSV file
        File tempFile = File.createTempFile("employees", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,firstName,lastName,salary,managerId\n");
            writer.write("1,Alice,Smith,2000,\n");
            writer.write("2,Bob,Jones,1500,1\n");
        }

        Map<String, Employee> employees = csvFileReader.readEmployeeDetails(tempFile.getAbsolutePath());

        assertEquals(2, employees.size());
        assertTrue(employees.containsKey("1"));
        assertTrue(employees.containsKey("2"));
        assertEquals(new BigDecimal("2000.00"), employees.get("1").getSalary());
        assertEquals("Alice", employees.get("1").getFirstName());
        assertEquals("1", employees.get("2").getManagerId());

        Files.deleteIfExists(tempFile.toPath());
    }

}
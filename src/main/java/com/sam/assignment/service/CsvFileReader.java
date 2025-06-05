package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.io.IOException;
import java.util.Map;

@Valid
public interface CsvFileReader {

    /**
     * Reads the employee details from a file and returns a map of employee IDs to Employee objects.
     *
     * @param filePath The path to the file containing employee details.
     * @return A map of employee IDs to Employee objects.
     * @throws IOException If an error occurs while reading the file.
     */
    Map<String, Employee> readEmployeeDetails(
        @NotEmpty(message = "")
        final String filePath) throws IOException;
}

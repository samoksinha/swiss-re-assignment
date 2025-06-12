package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.util.ValidationUtil;

import java.io.IOException;
import java.util.Map;

public interface CsvFileReader {

    /**
     * Reads employee details from a CSV file and returns a map of employee IDs to Employee objects.
     *
     * @param applicationParameter the parameters for the application, including the file path
     * @param validationUtil       utility for validating employee data
     * @return a map where keys are employee IDs and values are Employee objects
     * @throws IOException if an error occurs while reading the file
     */
    Map<String, Employee> readEmployeeDetails(
        final Parameter applicationParameter,
        final ValidationUtil validationUtil) throws IOException;
}

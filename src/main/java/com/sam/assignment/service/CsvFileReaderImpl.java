package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Subordinate;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CsvFileReaderImpl implements CsvFileReader {

    public static Logger log = LoggerFactory.getLogger(CsvFileReaderImpl.class);

    private final Validator validator;

    public CsvFileReaderImpl(
        @NotNull(message = "Validator cannot be null")
        final Validator validator) {
        this.validator = validator;
    }

    /**
     * Reads employee details from a CSV file and returns a map of employee IDs to Employee objects.
     * The first line of the CSV file is assumed to be a header and is skipped.
     *
     * @param filePath the path to the CSV file
     * @return a map containing employee IDs as keys and Employee objects as values
     * @throws IOException if an I/O error occurs while reading the file
     */
    @Override
    public Map<String, Employee> readEmployeeDetails(
        final String filePath) throws IOException {

        final Map<String, Employee> employees = new ConcurrentHashMap<>();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                count = count + 1;
                if(count == 1)
                    continue;

                String[] parts = line.split(",");
                Employee employee = new Employee(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts.length == 4 ? null : parts[4].trim(),
                        BigDecimal.valueOf(Double.parseDouble(parts[3].trim()))
                                .setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.ZERO,
                        0,
                        new Subordinate()
                );

                validator.validate(employee);
                employees.put(employee.getId(), employee);
            }
        }

        log.info(
            "Successfully read Employee records from CSV File. total-record-count : {} | file-path : {}",
            employees.size(),
            filePath
        );
        return employees;
    }

}

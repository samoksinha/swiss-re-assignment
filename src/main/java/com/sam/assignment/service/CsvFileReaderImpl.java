package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CsvFileReaderImpl implements CsvFileReader {

    /**
     * Reads employee details from a CSV file and returns a map of employee IDs to Employee objects.
     *
     * @param applicationParameter the parameters for the application, including the file path
     * @param validationUtil       utility for validating employee data
     * @return a map where keys are employee IDs and values are Employee objects
     * @throws IOException if an error occurs while reading the file
     */
    @Override
    public Map<String, Employee> readEmployeeDetails(
        final Parameter applicationParameter,
        final ValidationUtil validationUtil) throws IOException {

        final Map<String, Employee> employees = new ConcurrentHashMap<>();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(applicationParameter.getFilePath()))) {
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
                    0
                );

                validationUtil.validate(employee);
                employees.put(employee.getId(), employee);
            }
        }

        log.info(
            "Successfully read Employee records from CSV File. total-record-count : {} | file-path : {}",
            employees.size(),
            applicationParameter.getFilePath()
        );
        return employees;
    }

}

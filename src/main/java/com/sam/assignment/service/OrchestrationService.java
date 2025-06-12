package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.model.Response;
import com.sam.assignment.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class OrchestrationService {

    private final ValidationUtil validationUtil;
    private final CsvFileReader csvFileReader;
    private final OrgHierarchy<Employee> orgHierarchy;

    /**
     * Constructs an OrchestrationService with the provided dependencies.
     *
     * @param validationUtil utility for validating application parameters
     * @param csvFileReader  service for reading employee details from a CSV file
     * @param orgHierarchy    service for building the organizational hierarchy
     */
    public OrchestrationService(
        final ValidationUtil validationUtil,
        final CsvFileReader csvFileReader,
        final OrgHierarchy<Employee> orgHierarchy) {

        if(Objects.isNull(validationUtil))
            throw new IllegalArgumentException("ValidationUtil cannot be null");

        if(Objects.isNull(csvFileReader))
            throw new IllegalArgumentException("CsvFileReader cannot be null");

        this.validationUtil = validationUtil;
        this.csvFileReader = csvFileReader;
        this.orgHierarchy = orgHierarchy;
    }

    /**
     * Populates the application parameters from the provided command line arguments.
     * Validates the parameters and returns a Parameter object.
     *
     * @param applicationArguments command line arguments containing file path and parameters
     * @return a Parameter object containing the validated application parameters
     */
    private Parameter populateParameter(
        final String[] applicationArguments) {

        if (Objects.isNull(applicationArguments)
                || applicationArguments.length != 4) {
            log.error(
                "Please provide below mandatory application arguments : " +
                "\n1. File-Path \n2. Manager-Least-Pay-Percentage \n3. Manager-More-Pay-Percentage \n4. Threshold-Reporting-Line-Length."
            );
            throw new IllegalArgumentException("Application arguments cannot be null or empty");
        }

        log.info(
            "Below mandatory application arguments are provided : " +
            "\n1. File-Path : {} \n2. Manager-Least-Pay-Percentage : {} \n3. Manager-More-Pay-Percentage : {} \n4. Threshold-Reporting-Line-Length : {}",
            applicationArguments[0],
            applicationArguments[1],
            applicationArguments[2],
            applicationArguments[3]
        );

        Parameter applicationParameter = new Parameter(
            applicationArguments[0],
            BigDecimal.valueOf(Long.parseLong(applicationArguments[1])),
            BigDecimal.valueOf(Long.parseLong(applicationArguments[2])),
            Integer.parseInt(applicationArguments[3])
        );
        validationUtil.validate(applicationParameter);

        return applicationParameter;
    }

    /**
     * Orchestrates the process of reading employee details from a CSV file,
     * building the organizational hierarchy, and returning a response with the CEO
     * and various calculated details.
     *
     * @param applicationArguments command line arguments containing file path and parameters
     * @return a Response object containing the CEO and other calculated details
     * @throws IOException if an error occurs while reading the CSV file
     */
    public Response<Employee> doOrchestration(
        final String[] applicationArguments) throws IOException {

        final Parameter applicationParameter = populateParameter(
            applicationArguments
        );

        final Map<String, Employee> employees = csvFileReader.readEmployeeDetails(
            applicationParameter,
            validationUtil
        );

        final Employee ceo = orgHierarchy.buildHierarchy(
            employees,
            validationUtil,
            applicationParameter
        );
        log.info(
            "Successfully built the organizational hierarchy with CEO details | ID : {}, Name : {} {}",
            Objects.nonNull(ceo) ? ceo.getId() : "",
            Objects.nonNull(ceo) ? ceo.getFirstName() : "",
            Objects.nonNull(ceo) ? ceo.getLastName() : ""
        );

        Response<Employee> response = new Response<>(
            ceo,
            applicationParameter,
            orgHierarchy.populateLeastPaidManagers(employees),
            orgHierarchy.populateOverPaidManagers(employees),
            orgHierarchy.populateMaxReportingLineLength(
                employees,
                validationUtil,
                applicationParameter
            )
        );

        validationUtil.validate(response);
        return response;
    }

}

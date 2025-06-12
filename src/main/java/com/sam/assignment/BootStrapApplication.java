package com.sam.assignment;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Response;
import com.sam.assignment.service.*;
import com.sam.assignment.util.ValidationUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BootStrapApplication {

    /**
     * The main method to start the organizational hierarchy builder application.
     * It initializes the necessary services and orchestrates the reading of employee data,
     * validation, and building of the organizational hierarchy.
     *
     * @param args command line arguments, expected to contain the file path and parameters for processing
     */
    public static void main(String[] args) {
        log.info(
            "**************** Starting the organizational hierarchy builder application. ******************"
        );

        try {
            final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            final Validator validator = factory.getValidator();

            final ValidationUtil validationUtil = new ValidationUtil(validator);
            final OrgHierarchy<Employee> orgHierarchy = new OrgHierarchyImpl<>();
            final CsvFileReader csvFileReader = new CsvFileReaderImpl();
            final OrchestrationService orchestrationService = new OrchestrationService(
                validationUtil,
                csvFileReader,
                orgHierarchy
            );

            Response<Employee> response = orchestrationService.doOrchestration(args);
            response.getLeastPaidManagers()
                .forEach(employee -> {
                    log.info(
                        "Manager : {} {} | earns : {} | less than required : {} | Manager Salary : {}",
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getDifferenceOfSubordinatesAverageSalary().abs(),
                        employee.getSalary().add(employee.getDifferenceOfSubordinatesAverageSalary().abs()),
                        employee.getSalary()
                    );
                });

            response.getOverPaidManagers()
                .forEach(employee -> {
                    log.info(
                        "Manager : {} {} | earns : {} | more than required : {} | Manager Salary : {}",
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getDifferenceOfSubordinatesAverageSalary().abs(),
                        employee.getSalary().subtract(employee.getDifferenceOfSubordinatesAverageSalary()),
                        employee.getSalary()
                    );
                });

            response.getMaxReportingLineLengthEmployees()
                .forEach(employee -> {
                    log.info(
                        "Employee : {} {} | has reporting line too long-by : {} | actual-reporting-line : {} | threshold-reporting-lone : {}",
                        employee.getFirstName(),
                        employee.getLastName(),
                        (employee.getReportingLineLength() - response.getApplicationParameter().getMaxSubordinatesCount()),
                        employee.getReportingLineLength(),
                        response.getApplicationParameter().getMaxSubordinatesCount()
                    );
                });

        } catch (Exception e) {
            log.error(
                "An unexpected error occurred. error-message : {}",
                e.getMessage(),
                e
            );
        } finally {
            log.info(
                "**************** Finished the organizational hierarchy builder application. ******************"
            );
        }
    }

}
package com.sam.assignment;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.EmployeeSalaryAnalytics;
import com.sam.assignment.service.CsvFileReader;
import com.sam.assignment.service.CsvFileReaderImpl;
import com.sam.assignment.service.OrgHierarchy;
import com.sam.assignment.service.OrgHierarchyImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BootStrapApplication {

    public static Logger log = LoggerFactory.getLogger(BootStrapApplication.class);

    /**
     * The main method serves as the entry point for the organizational hierarchy builder application.
     * It reads command-line arguments, validates them, and then builds the organizational hierarchy
     * from employee details provided in a CSV file. It also performs salary analytics and reporting line checks.
     *
     * @param args Command-line arguments: file path, manager least pay percentage, manager more pay percentage,
     *             and threshold reporting line length.
     */
    public static void main(String[] args) {
        log.info(
            "**************** Starting the organizational hierarchy builder application. ******************"
        );
        try {
            if (args.length == 0) {
                log.error(
                    "Please provide below mandatory application arguments : " +
                    "\n1. File-Path \n2. Manager-Least-Pay-Percentage \n3. Manager-More-Pay-Percentage \n4. Threshold-Reporting-Line-Length."
                );
                return;
            }
            log.info(
                "Below mandatory application arguments are provided : " +
                "\n1. File-Path : {} \n2. Manager-Least-Pay-Percentage : {} \n3. Manager-More-Pay-Percentage : {} \n4. Threshold-Reporting-Line-Length : {}",
                args[0],
                args[1],
                args[2],
                args[3]
            );

            final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            final Validator validator = factory.getValidator();

            final String filepath = args[0];
            final BigDecimal managerLeastPayPercentage = new BigDecimal(args[1]).setScale(2, RoundingMode.HALF_UP);
            final BigDecimal managerOverPayPercentage = new BigDecimal(args[2]).setScale(2, RoundingMode.HALF_UP);
            final Integer maxReportingLineLength = Integer.parseInt(args[3]);

            final OrgHierarchy<Employee> orgHierarchy = new OrgHierarchyImpl<>(validator);
            final CsvFileReader csvFileReader = new CsvFileReaderImpl(validator);

            final Map<String, Employee> employees = csvFileReader.readEmployeeDetails(filepath);
            final Employee ceo = orgHierarchy.buildHierarchy(employees);
            log.info(
                "Successfully built the organizational hierarchy with CEO details | ID : {}, Name : {} {}",
                Objects.nonNull(ceo) ? ceo.getId() : "",
                Objects.nonNull(ceo) ? ceo.getFirstName() : "",
                Objects.nonNull(ceo) ? ceo.getLastName() : ""
            );

            final EmployeeSalaryAnalytics employeeSalaryAnalytics = orgHierarchy.performSalaryAnalytics(
                employees,
                managerLeastPayPercentage,
                managerOverPayPercentage
            );
            employeeSalaryAnalytics.getLeastPaidEmployees()
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
            employeeSalaryAnalytics.getOverPaidEmployees()
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

            final List<Employee> employeeReportingLines = orgHierarchy.populateEmployeeReportingLine(
                employees,
                Integer.parseInt(args[3])
            );
            employeeReportingLines
                .forEach(employee -> {
                    log.info(
                        "Employee : {} {} | has reporting line too long-by : {} | actual-reporting-line : {} | threshold-reporting-lone : {}",
                        employee.getFirstName(),
                        employee.getLastName(),
                        (employee.getReportingLineLength() - maxReportingLineLength),
                        employee.getReportingLineLength(),
                        maxReportingLineLength
                    );
                });

        } catch (IllegalArgumentException iae) {
            log.error(
                "Error building organizational hierarchy. error-message : "+
                iae.getMessage()
            );
            log.error(
                "IllegalArgumentException : Stacktrace : {}",
                iae.getMessage()
            );
        } catch (IOException ioe) {
            log.error(
                "Error reading employee details from file. error-message : "+
                ioe.getMessage()
            );
            log.error(
                "IOException : Stacktrace : {}",
                ioe.getMessage()
            );
        } catch (Exception e) {
            log.error(
                "An unexpected error occurred. error-message : "+
                e.getMessage()
            );
            log.error(
                "Exception : Stacktrace : {}",
                e.getMessage()
            );
        } finally {
            log.info(
                "**************** Finished the organizational hierarchy builder application. ******************"
            );
        }
    }

}
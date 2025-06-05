package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.EmployeeSalaryAnalytics;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Valid
public interface OrgHierarchy<T extends Employee> {

    /**
     * Builds the organizational hierarchy by linking employees to their respective managers.
     * This method iterates through the provided map of employees, identifies the CEO (the employee without a manager),
     * and populates the subordinate information for each employee under their respective manager.
     *
     * @param employees A map of employee IDs to Employee objects representing the organizational structure.
     * @return The CEO of the organization, which is the top-level employee in the hierarchy.
     */
    Employee buildHierarchy(
        @NotEmpty(message = "Employees map cannot be empty")
        final Map<String, Employee> employees
    );

    /**
     * Populates the reporting line of employees in the organizational hierarchy.
     * This method checks if the reporting line length of each employee does not exceed the specified maximum length.
     *
     * @param employees             A map of employee IDs to Employee objects representing the organizational structure.
     * @param maxReportingLineLength The maximum allowed length for the reporting line.
     * @return A list of employees whose reporting lines are valid.
     */
    List<Employee> populateEmployeeReportingLine(
        @NotEmpty(message = "Employees map cannot be empty")
        final Map<String, Employee> employees,
        @NotNull(message = "Max Reporting Line length cannot be null")
        @Positive(message = "Maz Reporting Line Length cannot be negative")
        final Integer maxReportingLineLength
    );

    /**
     * Performs salary analytics on the organizational hierarchy.
     * This method calculates the average salary of subordinates for each manager and identifies employees
     * whose salaries are below or above the specified thresholds.
     *
     * @param employees                   A map of employee IDs to Employee objects representing the organizational structure.
     * @param managerLeastPayPercentage   The percentage threshold for identifying managers with lower-than-average pay.
     * @param managerMorePayPercentage    The percentage threshold for identifying managers with higher-than-average pay.
     * @return An EmployeeSalaryAnalytics object containing the results of the salary analytics.
     */
    EmployeeSalaryAnalytics performSalaryAnalytics(
        @NotEmpty(message = "Employees map cannot be empty")
        final Map<String, Employee> employees,
        @NotNull(message = "Manager Least Pay Percentage cannot be null")
        @Positive(message = "Manager Least Pay Percentage cannot be negative")
        final BigDecimal managerLeastPayPercentage,
        @NotNull(message = "Manager More Pay Percentage cannot be null")
        @Positive(message = "Manager More Pay Percentage cannot be negative")
        final BigDecimal managerMorePayPercentage
    );

    /**
     * Populates the subordinate information for an employee under their manager.
     * This method updates the number of subordinates, the total salary of subordinates,
     * and the average salary of subordinates for the manager.
     *
     * @param employee The employee to be added as a subordinate.
     * @param manager  The manager under whom the employee is being added as a subordinate.
     */
    default void populateSubordinate(
        @NotNull(message = "Employee cannot be null")
        final Employee employee,
        @NotNull(message = "Manager cannot be null")
        final Employee manager) {

        manager.getSubordinate().setNumberOfSubordinates(
            manager.getSubordinate().getNumberOfSubordinates()
                .add(BigInteger.ONE));
        manager.getSubordinate().setSalarySumOfSubordinates(
            manager.getSubordinate().getSalarySumOfSubordinates()
                .add(employee.getSalary()));
        manager.getSubordinate().setSubordinatesAverageSalary(
            manager.getSubordinate().getSalarySumOfSubordinates()
                .divide(
                    BigDecimal.valueOf(manager.getSubordinate().getNumberOfSubordinates().intValue()),
                2,
                    RoundingMode.HALF_UP
                )
        );

        manager.getSubordinate().getEmployees()
            .put(employee.getId(), employee);
    }

    /**
     * Validates the employee object using the provided validator.
     * Throws an IllegalArgumentException if validation fails.
     *
     * @param validator The validator to use for validation.
     * @param employee  The employee object to validate.
     */
    default void validateEmployee(
        @NotNull(message = "Validator cannot be null")
        final Validator validator,
        @NotNull(message = "Employee cannot be null")
        final Employee employee) {

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);
        if(!violations.isEmpty())
            throw new IllegalArgumentException(
                "Validation failed for employee: " + employee.getId() + " - " +
                    violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                        .orElse("Unknown validation error")
            );
    }

    /**
     * Validates the employee salary analytics object using the provided validator.
     * Throws an IllegalArgumentException if validation fails.
     *
     * @param validator The validator to use for validation.
     * @param employeeSalaryAnalytics The employee salary analytics object to validate.
     */
    default void validateEmployeeSalaryAnalytics(
            @NotNull(message = "Validator cannot be null")
            final Validator validator,
            @NotNull(message = "Employee cannot be null")
            final EmployeeSalaryAnalytics employeeSalaryAnalytics) {

        Set<ConstraintViolation<EmployeeSalaryAnalytics>> violations = validator.validate(employeeSalaryAnalytics);
        if(!violations.isEmpty())
            throw new IllegalArgumentException(
                "Validation failed for Employee Analytics:  - " +
                    violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                        .orElse("Unknown validation error")
            );
    }

    /**
     * Finds the reporting line of an employee in the organizational hierarchy.
     * This method traverses up the hierarchy from the given employee to the CEO,
     * counting the number of levels in the reporting line.
     *
     * @param employee  The employee whose reporting line is to be found.
     * @param employees A map of employee IDs to Employee objects representing the organizational structure.
     * @return The number of levels in the reporting line from the given employee to the CEO.
     */
    default Integer findEmployeeReportingLine(
        @NotNull(message = "CEO cannot be null")
        final Employee employee,
        @NotEmpty(message = "Employees map cannot be empty")
        final Map<String, Employee> employees) {

        Integer employeeReportingLine = 0;
        Employee currentEmployee = employee;
        while(currentEmployee.getManagerId() != null) {
            currentEmployee = employees.get(currentEmployee.getManagerId());
            employeeReportingLine++;
        }

        return employeeReportingLine;
    }

}

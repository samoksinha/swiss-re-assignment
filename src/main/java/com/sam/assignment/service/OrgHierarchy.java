package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface OrgHierarchy<T extends Employee> {

    /**
     * Builds the organizational hierarchy from a map of employees.
     *
     * @param employees          a map of all employees indexed by their IDs
     * @param validationUtil     utility for validating employee objects
     * @param applicationParameter parameters for application-specific calculations
     * @return the root employee (CEO) of the organizational hierarchy
     */
    Employee buildHierarchy(
        final Map<String, Employee> employees,
        final ValidationUtil validationUtil,
        final Parameter applicationParameter
    );

    /**
     * Populates the maximum reporting line length for each employee in the organization.
     *
     * @param employees          a map of all employees indexed by their IDs
     * @param validationUtil     utility for validating employee objects
     * @param applicationParameter parameters for application-specific calculations
     * @return a list of employees with their reporting line lengths populated
     */
    List<Employee> populateMaxReportingLineLength(
        final Map<String, Employee> employees,
        final ValidationUtil validationUtil,
        final Parameter applicationParameter
    );

    /**
     * Populates the difference of subordinates' average salary for each manager.
     *
     * @param manager            the manager whose subordinates' average salary is to be calculated
     * @param applicationParameter parameters for application-specific calculations
     */
    default void populateDifferenceOfSubordinatesAverageSalary(
        final Employee manager,
        final Parameter applicationParameter) {

        BigDecimal subordinatesAverageSalary = BigDecimal.valueOf(manager.getSubordinates()
            .stream()
            .mapToDouble(employee -> {
                if(Objects.isNull(employee.getSalary()))
                    return 0.0;

                return employee.getSalary().doubleValue();
            })
            .average()
            .orElse(0.0)
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal minSalary = subordinatesAverageSalary
                .multiply(applicationParameter.getManagerLeastPayPercentage()
                        .add(BigDecimal.valueOf(100)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal maxSalary = subordinatesAverageSalary
                .multiply(applicationParameter.getManagerOverPayPercentage()
                        .add(BigDecimal.valueOf(100)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if(manager.getSalary().compareTo(minSalary) < 0)
            manager.setDifferenceOfSubordinatesAverageSalary(manager.getSalary().subtract(minSalary));
        else if(manager.getSalary().compareTo(maxSalary) > 0)
            manager.setDifferenceOfSubordinatesAverageSalary(manager.getSalary().subtract(maxSalary));
    }

    /**
     * Populates the list of managers who are paid less than their subordinates' average salary.
     *
     * @param employees a map of all employees indexed by their IDs
     * @return a list of managers who are underpaid
     */
    default List<Employee> populateLeastPaidManagers(
        final Map<String, Employee> employees) {

        return employees.values()
            .stream()
            .map(employee -> {
                if(employee.getDifferenceOfSubordinatesAverageSalary()
                        .compareTo(BigDecimal.ZERO) < 0) {
                    return employee;
                }

                return null;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Populates the list of managers who are paid more than their subordinates' average salary.
     *
     * @param employees a map of all employees indexed by their IDs
     * @return a list of managers who are overpaid
     */
    default List<Employee> populateOverPaidManagers(
        final Map<String, Employee> employees) {

        return employees.values()
            .stream()
            .map(employee -> {
                if(employee.getDifferenceOfSubordinatesAverageSalary()
                        .compareTo(BigDecimal.ZERO) > 0) {
                    return employee;
                }

                return null;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Calculates the length of the reporting line for a given employee.
     *
     * @param employee  the employee whose reporting line length is to be calculated
     * @param employees a map of all employees indexed by their IDs
     * @return the length of the reporting line
     */
    default Integer getReportingLineLength(
        final Employee employee,
        final Map<String, Employee> employees) {

        Integer reportingLineLength = 0;
        Employee currentEmployee = employee;
        while(currentEmployee.getManagerId() != null) {
            currentEmployee = employees.get(currentEmployee.getManagerId());
            reportingLineLength++;
        }

        return reportingLineLength;
    }

}

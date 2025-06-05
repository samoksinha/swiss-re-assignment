package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.EmployeeSalaryAnalytics;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class OrgHierarchyImpl<T extends Employee> implements OrgHierarchy<Employee> {

    private final Validator validator;

    public OrgHierarchyImpl(
        @NotNull(message = "Validator cannot be null")
        final Validator validator) {
        this.validator = validator;
    }

    /**
     * Builds the organizational hierarchy by linking employees to their respective managers.
     * This method iterates through the provided map of employees, identifies the CEO (the employee without a manager),
     * and populates the subordinate information for each employee under their respective manager.
     *
     * @param employees A map of employee IDs to Employee objects representing the organizational structure.
     * @return The CEO of the organization, which is the top-level employee in the hierarchy.
     */
    @Override
    public Employee buildHierarchy(
        final Map<String, Employee> employees) {

        AtomicReference<Employee> ceo = new AtomicReference<>();
        employees.forEach((id, employee) -> {

            if(StringUtils.isEmpty(employee.getManagerId()))
                ceo.set(employee);
            else {
                populateSubordinate(
                    employee,
                    employees.get(employee.getManagerId())
                );

                employee.setReportingLineLength(
                    findEmployeeReportingLine(employee, employees)
                );
            }

            validateEmployee(validator, employee);
        });

        return ceo.get();
    }

    /**
     * Populates the subordinate information for an employee under their respective manager.
     * This method updates the subordinate count and salary sum for the manager based on the employee's details.
     *
     * @param employee The employee whose details are to be added to the manager's subordinates.
     * @param manager  The manager under whom the employee is being added.
     */
    @Override
    public List<Employee> populateEmployeeReportingLine(
            final Map<String, Employee> employees,
            final Integer maxReportingLineLength) {

        return employees.values()
            .stream()
            .map(employee -> {
                if(employee.getReportingLineLength() > maxReportingLineLength)
                    return employee;

                return null;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Performs salary analytics on the organizational hierarchy.
     * This method identifies employees who are paid less than a specified percentage of their manager's salary
     * and those who are paid more than a specified percentage of their manager's salary.
     *
     * @param employees                   A map of employee IDs to Employee objects representing the organizational structure.
     * @param managerLeastPayPercentage   The percentage below which an employee is considered underpaid compared to their manager.
     * @param managerMorePayPercentage    The percentage above which an employee is considered overpaid compared to their manager.
     * @return An EmployeeSalaryAnalytics object containing lists of underpaid and overpaid employees.
     */
    @Override
    public EmployeeSalaryAnalytics performSalaryAnalytics(
            final Map<String, Employee> employees,
            final BigDecimal managerLeastPayPercentage,
            final BigDecimal managerMorePayPercentage) {

        final EmployeeSalaryAnalytics employeeSalaryAnalytics = new EmployeeSalaryAnalytics();
        employees.values()
            .stream()
            .filter(employee -> {
                if(employee.getSubordinate().getSubordinatesAverageSalary().equals(BigDecimal.ZERO))
                    return false;

                return true;
            })
            .forEach(employee -> {

                BigDecimal minSalary = employee.getSubordinate()
                    .getSubordinatesAverageSalary()
                    .multiply(managerLeastPayPercentage.add(BigDecimal.valueOf(100)))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal maxSalary = employee.getSubordinate()
                    .getSubordinatesAverageSalary()
                    .multiply(managerMorePayPercentage.add(BigDecimal.valueOf(100)))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                if(employee.getSalary().compareTo(minSalary) < 0) {
                    employee.setDifferenceOfSubordinatesAverageSalary(employee.getSalary().subtract(minSalary));
                    employeeSalaryAnalytics.getLeastPaidEmployees().add(employee);
                }
                else if(employee.getSalary().compareTo(maxSalary) > 0) {
                    employee.setDifferenceOfSubordinatesAverageSalary(employee.getSalary().subtract(maxSalary));
                    employeeSalaryAnalytics.getOverPaidEmployees().add(employee);
                }

                validateEmployee(validator, employee);
            });

        validateEmployeeSalaryAnalytics(validator, employeeSalaryAnalytics);
        return employeeSalaryAnalytics;
    }

}

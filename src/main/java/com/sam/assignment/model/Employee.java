package com.sam.assignment.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an employee in the organization.
 * Contains fields for employee ID, first name, last name, manager ID, salary,
 * difference of subordinates' average salary, reporting line length, and a list of subordinates.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Employee implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @NotEmpty(message = "Employee ID cannot be empty")
        @Size(min = 10, max = 10, message = "Employee ID must be exactly 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Employee ID must be alphanumeric")
        private final String id;

        @NotEmpty(message = "Employee First Name cannot be empty")
        @Size(max = 50, message = "Employee First Name must be at most 50 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Employee First Name must contain only letters")
        private final String firstName;

        @NotEmpty(message = "Employee Last Name cannot be empty")
        @Size(max = 50, message = "Employee Last Name must be at most 50 characters")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "Employee Last Name must contain only letters")
        private final String lastName;

        @Size(max = 10, message = "Manager ID must be at most 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Manager ID must be alphanumeric")
        private final String managerId;

        @DecimalMax(value = "1000000000", message = "Salary cannot be more than 1 billion")
        @NotNull(message = "Salary cannot be null")
        @DecimalMin(value = "0", message = "Salary cannot be negative")
        private final BigDecimal salary;

        @NotNull(message = "Difference of subordinates average salary cannot be null")
        @DecimalMax(value = "1000000000", message = "Difference Of Subordinates Average Salary cannot be more than 1 billion")
        private BigDecimal differenceOfSubordinatesAverageSalary = BigDecimal.ZERO;

        @NotNull(message = "Reporting line length cannot be null")
        @DecimalMin(value = "0", message = "Reporting line length cannot be negative")
        @DecimalMax(value = "1000", message = "Reporting line length cannot be more than 1000")
        private Integer reportingLineLength = 0;

        @Valid
        @NotNull(message = "Number of subordinates cannot be null")
        private final List<Employee> subordinates = new LinkedList<>();

}

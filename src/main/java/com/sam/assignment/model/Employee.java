package com.sam.assignment.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Employee implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @NotEmpty(message = "Employee ID cannot be empty")
        private final String id;

        @NotEmpty(message = "Employee First Name cannot be empty")
        private final String firstName;

        @NotEmpty(message = "Employee Last Name cannot be empty")
        private final String lastName;

        private final String managerId;

        @NotNull(message = "Salary cannot be null")
        @Positive(message = "Salary cannot be negative")
        private final BigDecimal salary;

        @NotNull(message = "Difference of subordinates average salary cannot be null")
        private BigDecimal differenceOfSubordinatesAverageSalary = BigDecimal.ZERO;

        @NotNull(message = "Reporting line length cannot be null")
        @DecimalMin(value = "0", inclusive = true, message = "Reporting line length cannot be negativee")
        private Integer reportingLineLength = 0;

        @Valid
        @NotNull(message = "Subordinate information cannot be null")
        private Subordinate subordinate;

}

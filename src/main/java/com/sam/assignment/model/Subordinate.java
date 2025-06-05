package com.sam.assignment.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Subordinate implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @NotNull(message = "Number of subordinates cannot be null")
    @DecimalMax(value = "1000", message = "Number of subordinates cannot be more than 1000")
    @DecimalMin(value = "0", inclusive = true, message = "Number of subordinates cannot be negative")
    private BigInteger numberOfSubordinates = BigInteger.ZERO;

    @NotNull(message = "Salary sum of subordinates cannot be null")
    @DecimalMax(value = "1000000000", message = "Salary sum of subordinates cannot be more than 1 billion")
    @DecimalMin(value = "0", inclusive = true, message = "Salary sum of subordinates cannot be negative")
    private BigDecimal salarySumOfSubordinates = BigDecimal.ZERO;

    @NotNull(message = "Average salary of subordinates cannot be null")
    @DecimalMax(value = "1000000000", message = "Average salary of subordinates cannot be more than 1 billion")
    @DecimalMin(value = "0", inclusive = true, message = "Average salary of subordinates cannot be negative")
    private BigDecimal subordinatesAverageSalary = BigDecimal.ZERO;

    @Valid
    @NotNull(message = "Subordinates cannot be empty")
    private Map<String, Employee> employees = new HashMap<>();

}

package com.sam.assignment.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
    @DecimalMin(value = "0", inclusive = true, message = "Number of subordinates cannot be negative")
    private BigInteger numberOfSubordinates = BigInteger.ZERO;

    @NotNull(message = "Salary sum of subordinates cannot be null")
    @DecimalMin(value = "0", inclusive = true, message = "Salary sum of subordinates cannot be negative")
    private BigDecimal salarySumOfSubordinates = BigDecimal.ZERO;

    @NotNull(message = "Average salary of subordinates cannot be null")
    @DecimalMin(value = "0", inclusive = true, message = "Average salary of subordinates cannot be negative")
    private BigDecimal subordinatesAverageSalary = BigDecimal.ZERO;

    @Valid
    @NotNull(message = "Subordinates cannot be empty")
    private Map<String, Employee> employees = new HashMap<>();

}

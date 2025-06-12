package com.sam.assignment.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents the parameters for the application, including file path,
 * manager least pay percentage, manager over pay percentage, and max subordinates count.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Parameter implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    @NotEmpty(message = "File path cannot be empty")
    private final String filePath;

    @NotNull(message = "Manager least pay percentage cannot be null")
    private final BigDecimal managerLeastPayPercentage;

    @NotNull(message = "Manager over pay percentage cannot be null")
    private final BigDecimal managerOverPayPercentage;

    @NotNull(message = "Max subordinates count cannot be null")
    @DecimalMin(value = "0", inclusive = true, message = "Max subordinates count cannot be negative")
    @DecimalMax(value = "999", message = "Reporting line length cannot be more than 999")
    private final Integer maxSubordinatesCount;

}

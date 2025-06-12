package com.sam.assignment.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Represents the response of the application, containing details about the CEO,
 * application parameters, and lists of managers based on their salary and reporting line length.
 *
 * @param <T> the type of employee (e.g., Employee)
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Response<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    @Valid
    @NotNull(message = "CEO cannot be null")
    private final T ceo;

    @Valid
    @NotNull(message = "Application Parameter cannot be null")
    private final Parameter applicationParameter;

    @Valid
    @NotNull(message = "Least Paid Managers cannot be null")
    private final List<T> leastPaidManagers;

    @Valid
    @NotNull(message = "Over Paid Managers cannot be null")
    private final List<T> overPaidManagers;

    @Valid
    @NotNull(message = "Max Reporting Line Length Employees cannot be null")
    private final List<T> maxReportingLineLengthEmployees;

}

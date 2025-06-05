package com.sam.assignment.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EmployeeSalaryAnalytics {

    @Valid
    @NotNull(message = "Employee with least salary cannot be null")
    private final List<Employee> leastPaidEmployees = new LinkedList<>();

    @Valid
    @NotNull(message = "Employee with over paid salary cannot be null")
    private final List<Employee> overPaidEmployees = new LinkedList<>();

}

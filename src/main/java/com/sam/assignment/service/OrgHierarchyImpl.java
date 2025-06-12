package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class OrgHierarchyImpl<T extends Employee> implements OrgHierarchy<Employee> {

    /**
     * Calculates the length of the reporting line for a given employee.
     *
     * @param employee  the employee whose reporting line length is to be calculated
     * @param employees a map of all employees indexed by their IDs
     * @return the length of the reporting line
     */
    @Override
    public Employee buildHierarchy(
        final Map<String, Employee> employees,
        final ValidationUtil validationUtil,
        final Parameter applicationParameter) {

        AtomicReference<Employee> ceo = new AtomicReference<>();
        employees.forEach((id, employee) -> {

            if(StringUtils.isEmpty(employee.getManagerId())) {
                ceo.set(employee);
            } else {
                Employee manager = employees.get(employee.getManagerId());
                if(Objects.nonNull(manager)) {
                    manager.getSubordinates().add(employee);
                    populateDifferenceOfSubordinatesAverageSalary(
                        manager,
                        applicationParameter
                    );
                }

                validationUtil.validate(manager);
            }
        });

        return ceo.get();
    }

    /**
     * Calculates the length of the reporting line for a given employee.
     *
     * @param employee  the employee whose reporting line length is to be calculated
     * @param employees a map of all employees indexed by their IDs
     * @return the length of the reporting line
     */
    @Override
    public List<Employee> populateMaxReportingLineLength(
        final Map<String, Employee> employees,
        final ValidationUtil validationUtil,
        final Parameter applicationParameter) {

        return employees.values()
            .stream()
            .map(employee -> {
                Integer reportingLineLength = getReportingLineLength(employee, employees);
                employee.setReportingLineLength(reportingLineLength);
                validationUtil.validate(employee);

                if(reportingLineLength > applicationParameter.getMaxSubordinatesCount())
                    return employee;

                return null;
            })
            .filter(Objects::nonNull)
            .toList();
    }

}

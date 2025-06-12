package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.Parameter;
import com.sam.assignment.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrgHierarchyImplTest {

    private OrgHierarchyImpl<Employee> orgHierarchy;
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        orgHierarchy = new OrgHierarchyImpl<>();
        validationUtil = mock(ValidationUtil.class);
    }

    @Test
    void testBuildHierarchy_CEOAndSubordinates() {
        Employee ceo = new Employee(
                "1",
                "Alice",
                "Smith",
                null,
                new BigDecimal("5000"),
                BigDecimal.ZERO,
                0
        );

        Employee emp = new Employee(
                "2",
                "Bob",
                "Jones",
                "1",
                new BigDecimal("2000"),
                BigDecimal.ZERO,
                0
        );

        Map<String, Employee> employees = new HashMap<>();
        employees.put(ceo.getId(), ceo);
        employees.put(emp.getId(), emp);

        Parameter param = new Parameter("file.csv", BigDecimal.ZERO, BigDecimal.ZERO, 0);

        Employee resultCeo = orgHierarchy.buildHierarchy(employees, validationUtil, param);

        assertEquals(ceo, resultCeo);
        assertTrue(ceo.getSubordinates().contains(emp));
        verify(validationUtil, atLeastOnce()).validate(any());
    }

    @Test
    void testPopulateMaxReportingLineLength_ReturnsCorrectEmployees() {
        // CEO
        Employee ceo = new Employee(
                "1",
                "Alice",
                "Smith",
                null,
                new BigDecimal("5000"),
                BigDecimal.ZERO,
                0
        );
        // Manager
        Employee mgr = new Employee(
                "2",
                "Bob",
                "Jones",
                "1",
                new BigDecimal("3000"),
                BigDecimal.ZERO,
                0
        );
        // Employee
        Employee emp = new Employee(
                "3",
                "Carol",
                "White",
                "2",
                new BigDecimal("2000"),
                BigDecimal.ZERO,
                0
        );

        Map<String, Employee> employees = new HashMap<>();
        employees.put(ceo.getId(), ceo);
        employees.put(mgr.getId(), mgr);
        employees.put(emp.getId(), emp);

        Parameter param = new Parameter(
                "file.csv",
                BigDecimal.ZERO,
                BigDecimal.valueOf(40),
                1
        );

        List<Employee> result = orgHierarchy.populateMaxReportingLineLength(
                employees,
                validationUtil,
                param
        );

        // Only CEO has reporting line length > 1 (CEO->mgr->emp = 2)
        assertEquals(1, result.size());
        assertEquals("3", result.get(0).getId());
        assertEquals(2, result.get(0).getReportingLineLength());
        verify(validationUtil, atLeast(1)).validate(any(Employee.class));
    }

}
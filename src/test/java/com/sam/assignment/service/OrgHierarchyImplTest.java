package com.sam.assignment.service;

import com.sam.assignment.model.Employee;
import com.sam.assignment.model.EmployeeSalaryAnalytics;
import com.sam.assignment.model.Subordinate;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrgHierarchyImplTest {

    private OrgHierarchyImpl<Employee> orgHierarchy;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Mockito.mock(Validator.class);
        orgHierarchy = new OrgHierarchyImpl<>(validator);
    }

    @Test
    void testBuildHierarchy_CeoAndSubordinates() {
        Employee ceo = new Employee("0000000001", "Alice", "Smith", null,
                new BigDecimal("2000"), BigDecimal.ZERO, 0, new Subordinate());
        Employee emp1 = new Employee("0000000002", "Bob", "Jones", "0000000001",
                new BigDecimal("1500"), BigDecimal.ZERO, 0, new Subordinate());
        Employee emp2 = new Employee("0000000003", "Carol", "White", "0000000002",
                new BigDecimal("1200"), BigDecimal.ZERO, 0, new Subordinate());

        Map<String, Employee> employees = new HashMap<>();
        employees.put(ceo.getId(), ceo);
        employees.put(emp1.getId(), emp1);
        employees.put(emp2.getId(), emp2);

        Employee result = orgHierarchy.buildHierarchy(employees);

        assertNotNull(result);
        assertEquals("0000000001", result.getId());
        assertEquals(1, emp1.getReportingLineLength());
        assertEquals(2, emp2.getReportingLineLength());
    }

    void testPopulateEmployeeReportingLine_ExceedsMaxReportingLineLength() {
        Employee emp1 = new Employee("0000000001", "Alice", "Smith", null,
                new BigDecimal("2000"), BigDecimal.ZERO, 1, new Subordinate());
        Employee emp2 = new Employee("0000000002", "Bob", "Jones", "0000000001",
                new BigDecimal("1500"), BigDecimal.ZERO, 2, new Subordinate());
        Employee emp3 = new Employee("0000000003", "Carol", "White", "0000000002",
                new BigDecimal("1200"), BigDecimal.ZERO, 3, new Subordinate());

        Map<String, Employee> employees = new HashMap<>();
        employees.put(emp1.getId(), emp1);
        employees.put(emp2.getId(), emp2);
        employees.put(emp3.getId(), emp3);

        // Only emp3 has reportingLineLength > 2
        List<Employee> result = orgHierarchy.populateEmployeeReportingLine(employees, 2);

        assertEquals(1, result.size());
        assertTrue(result.contains(emp3));
    }

    @Test
    void testPerformSalaryAnalytics_UnderpaidAndOverpaid() {
        Employee manager = new Employee("0000000001", "John", "Doe", null,
                new BigDecimal("1200"), BigDecimal.ZERO, 0, new Subordinate());
        manager.getSubordinate().setSubordinatesAverageSalary(new BigDecimal("600"));

        Employee underpaid = new Employee("0000000002", "Under", "Paid", "0000000001",
                new BigDecimal("600"), BigDecimal.ZERO, 0, new Subordinate());
        underpaid.getSubordinate().setSubordinatesAverageSalary(new BigDecimal("1000"));

        Employee overpaid = new Employee("0000000003", "Over", "Paid", "0000000002",
                new BigDecimal("1000"), BigDecimal.ZERO, 0, new Subordinate());
        overpaid.getSubordinate().setSubordinatesAverageSalary(BigDecimal.ZERO);

        Map<String, Employee> employees = new HashMap<>();
        employees.put(manager.getId(), manager);
        employees.put(underpaid.getId(), underpaid);
        employees.put(overpaid.getId(), overpaid);

        // 10% thresholds
        BigDecimal leastPayPct = new BigDecimal("10");
        BigDecimal morePayPct = new BigDecimal("10");

        EmployeeSalaryAnalytics analytics = orgHierarchy.performSalaryAnalytics(
                employees, leastPayPct, morePayPct);

        assertNotNull(analytics);
        assertEquals(1, analytics.getLeastPaidEmployees().size()); // manager is not underpaid
        assertEquals(1, analytics.getOverPaidEmployees().size()); // manager is not overpaid
    }

    @Test
    void testPerformSalaryAnalytics_ManagerUnderpaid() {
        Employee manager = new Employee("0000000001", "John", "Doe", null,
                new BigDecimal("850"), BigDecimal.ZERO, 0, new Subordinate());
        manager.getSubordinate().setSubordinatesAverageSalary(new BigDecimal("800"));

        Map<String, Employee> employees = new HashMap<>();
        employees.put(manager.getId(), manager);

        BigDecimal leastPayPct = new BigDecimal("10"); // 10% above avg = 880
        BigDecimal morePayPct = new BigDecimal("10");

        EmployeeSalaryAnalytics analytics = orgHierarchy.performSalaryAnalytics(
                employees, leastPayPct, morePayPct);

        assertEquals(1, analytics.getLeastPaidEmployees().size());
        assertTrue(analytics.getLeastPaidEmployees().contains(manager));
    }

    @Test
    void testPerformSalaryAnalytics_ManagerOverpaid() {
        Employee manager = new Employee("0000000001", "John", "Doe", null,
                new BigDecimal("1000"), BigDecimal.ZERO, 0, new Subordinate());
        manager.getSubordinate().setSubordinatesAverageSalary(new BigDecimal("800"));

        Map<String, Employee> employees = new HashMap<>();
        employees.put(manager.getId(), manager);

        BigDecimal leastPayPct = new BigDecimal("10");
        BigDecimal morePayPct = new BigDecimal("10"); // 10% above avg = 880

        EmployeeSalaryAnalytics analytics = orgHierarchy.performSalaryAnalytics(
                employees, leastPayPct, morePayPct);

        assertEquals(1, analytics.getOverPaidEmployees().size());
        assertTrue(analytics.getOverPaidEmployees().contains(manager));
    }

    @Test
    void testPerformSalaryAnalytics_EmptyEmployees() {
        Map<String, Employee> employees = new HashMap<>();
        EmployeeSalaryAnalytics analytics = orgHierarchy.performSalaryAnalytics(
                employees, BigDecimal.TEN, BigDecimal.TEN);

        assertNotNull(analytics);
        assertTrue(analytics.getLeastPaidEmployees().isEmpty());
        assertTrue(analytics.getOverPaidEmployees().isEmpty());
    }

}

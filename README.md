************ Assumptions ************

**** Employee Entity ****
employeeId: Required, alphanumeric, max 10 characters.
firstName: Required, alphabetic, max 50 characters.
lastName: Required, alphabetic, max 50 characters.
managerId: Optional, alphanumeric, max 10 characters.
salary: BigDecimal, positive, not null, max 1,000,000,000.
differenceOfSubordinatesAverageSalary BigDecimal, positive/negative not null, max 1,000,000,000.
reportingLineLength: Integer, 0 to 1000, not null.
subordinates: List of Employee, not null, can be empty.

******* Parameter Entity ******
filePath: Required, alphanumeric.
managerLeastPayPercentage: BigDecimal, not null.
managerOverPayPercentage: BigDecimal, not null.
maxSubordinatesCount: Integer, 0 to 999, not null.

******* Response Entity ******
ceo: Employee, not null.
applicationParameter: Parameter, not null.
leastPaidManagers: List of Least Paid Employee, not null, can be empty.
overPaidManagers: List of Over Paid Employee, not null, can be empty.
maxReportingLineLengthEmployees: List of Employees with max reporting line length, not null, can be empty.

************ CSV File Content ************
Header : Id,firstName,lastName,salary,managerId
managerId can be empty only for CEO
Other validations are specified in the Employee Entity section.

************ Project Details ************
JDK : 17
Maven : 4.0.0
Fatty Jar : Yes (through maven-shade-plugin) (possible-name : swiss-re-assignment-1.0-SNAPSHOT.jar)
Test Cases : Yes (JUnit 4.13.2, Powermock 2.0.9, Mockito 4.11.0)

************ How to run the project ************
Main Class : com.sam.assignment.BootStrapApplication

Can be run using the command prompt or terminal using fatty jar or through IDE/Maven.

Application Arguments:
1. File-Path (String) - Path to the CSV file containing employee details.
2. Manager-Least-Pay-Percentage (BigDecimal) - The minimum percentage of salary that a manager should earn compared to their subordinates.
3. Manager-More-Pay-Percentage (BigDecimal) - The maximum percentage of salary that a manager can earn compared to their subordinates.
4. Threshold-Reporting-Line-Length. (Integer) - The maximum number of managers between an employee and the CEO in the reporting line.

Command to run the fatty jar:
java -jar <jar-path> <path-to-csv-file> <least-percentage> <upper-percentage> <number-of-managers-between-employee-ceo>
Example: java -jar swiss-re-assignment-1.0-SNAPSHOT.jar C:\applications\swiss-re-assignment\main\resource\employee-details.csv 20 40 2


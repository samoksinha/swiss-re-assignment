************ Assumptions ************
**** Employee Entity ****
employeeId: Required, alphanumeric, max 10 characters.
firstName: Required, alphabetic, max 50 characters.
lastName: Required, alphabetic, max 50 characters.
managerId: Optional, alphanumeric, max 10 characters.
salary: BigDecimal, positive, not null, max 1,000,000,000.
differenceOfSubordinatesAverageSalary BigDecimal, positive/negative not null, max 1,000,000,000.
reportingLineLength: Integer, 0 to 1000, not null.

******* Subordinate Entity ******
numberOfSubordinates: BigInteger, 0 to 1000, not null.
salarySumOfSubordinates BigDecimal, positive, not null, max 1,000,000,000.
subordinatesAverageSalary BigDecimal, positive/negative not null, max 1,000,000,000.

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

Command to run the fatty jar:
java -jar <jar-path> <path-to-csv-file> <least-percentage> <upper-percentage> <number-of-managers-between-employee-ceo>
Example: java -jar swiss-re-assignment-1.0-SNAPSHOT.jar C:\applications\swiss-re-assignment\main\resource\employee-details.csv 20 40 2


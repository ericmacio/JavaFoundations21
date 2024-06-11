package com.eric.employees;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Employee implements IEmployee {
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
    private final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
    private static final String PEOPLE_REGEX =
            "(?<lastName>\\w+),\\s*(?<firstName>\\w+),\\s*(?<dob>\\d{1,2}/\\d{1,2}/\\d{4}),\\s*(?<role>\\w+)(?:,\\s*\\{(?<details>.*)\\})?";
    public static final Pattern PEOPLE_PAT = Pattern.compile(PEOPLE_REGEX);
    protected final Matcher peopleMat;
    protected static final Predicate<Employee> dummySelector = e -> "N/A".equals(e.getLastName());

    private Employee() {
        peopleMat = null;
        this.firstName = "N/A";
        this.lastName = "N/A";
        this.dob = LocalDate.from(dtFormatter.parse("01/01/2000"));
    }

    protected Employee(String personText) {
        peopleMat = PEOPLE_PAT.matcher(personText);
        if(peopleMat.find()) {
            this.lastName = peopleMat.group("lastName");
            this.firstName = peopleMat.group("firstName");
            this.dob = LocalDate.from(dtFormatter.parse(peopleMat.group("dob")));
        }
    }

    public static IEmployee createEmployee(String employeeText) {
        Matcher peopleMat = PEOPLE_PAT.matcher(employeeText);
        if(peopleMat.find()) {
            return switch (peopleMat.group("role")) {
                case "Programmer" -> new Programmer(peopleMat.group());
                case "Manager" -> new Manager(peopleMat.group());
                case "Analyst" -> new Analyst(peopleMat.group());
                case "CEO" -> new CEO(peopleMat.group());
                default -> new DummyEmployee();
            };
        } else {
            return new DummyEmployee();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public abstract int getSalary();

    @Override
    public String toString() {
        return String.format("%s, %s, %s: %s - %s", lastName, firstName, dob, moneyFormat.format(getSalary()), moneyFormat.format(getBonus()));
    }

    public double getBonus() {
        return getSalary() * 0.1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return firstName.equals(employee.firstName)
                && lastName.equals(employee.lastName)
                && dob.equals(employee.dob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, dob);
    }

    public int compareTo(IEmployee iemployee) {
        Employee emp = (Employee) iemployee;
        return lastName.compareTo(emp.getLastName());
    }

    private static final class DummyEmployee extends Employee  {
        @Override
        public int getSalary() {
            return 0;
        }
    }
}

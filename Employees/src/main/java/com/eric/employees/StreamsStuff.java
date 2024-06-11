package com.eric.employees;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamsStuff {
    private static final String filePath =
            "/home/eric/Software/IdeaProjects/JavaFoundations21/Employees/src/main/java/com/eric/employees/employee.txt";
    public static void main(String[] args) {
        //Employees input
        String peopleText = """
            Cruise1, Fred, 1/1/1900, Programmer, {locpd=3000,yoe=10,iq=140}
            Cruise1, Fred, 1/1/1900, Programmer, {locpd=400,yoe=5,iq=140}
            Cruise2, Fred, 1/1/1900, Programmer, {locpd=4000,yoe=10,iq=140}
            Cruise3, Fred, 1/1/1900, Programmer, {locpd=5000,yoe=10,iq=140}
            Cruise4, Fred, 1/1/1900, Programmer, {locpd=6000,yoe=10,iq=140}
            Cruise5, Fred, 1/1/1900, Programmer, {locpd=7000,yoe=10,iq=140}
            CruiseBad, Fred, 1/1/1900, Programmer, {locpd=7000,yoe=10,iq=140}
            Smith1, Wilma, 3/3/1910, Analyst, {projectCount=3}
            Smith2, Wilma2, 3/3/1910, Analyst, {projectCount=4}
            Smith3, Wilma3, 3/3/1910, Analyst, {projectCount=5}
            Smith4, Wilma4, 3/3/1910, Analyst, {projectCount=6}
            Smith5, Wilma5, 3/3/1910, Analyst, {projectCount=9}
            SmithBad, Wilma5, 3/3/1910, Analyst, {projectCount=9}
            Barton, Betty, 4/4/1915, CEO, {avgStockPrice=300}
            BadEmployee, Wilma5, 3/3/1910, Analystxxxxxx, {projectCount=9}
            Rubble1, Barney, 2/2/1905, Manager, {orgSize=300,dr=10}
            Rubble2, Barney2, 2/2/1905, Manager, {orgSize=100,dr=4}
            Rubble3, Barney3, 2/2/1905, Manager, {orgSize=200,dr=2}
            Rubble4, Barney4, 2/2/1905, Manager, {orgSize=500,dr=8}
            Rubble5, Barney5, 2/2/1905, Manager, {orgSize=175,dr=20}
            RubbleBad, Barney5, 2/2/1905, Manager, {orgSize=175,dr=20}
            """;

        int totalSalaries = peopleText.lines()
                .map(Employee::createEmployee)
                .map(e -> (Employee)e)
                .distinct()
                .sorted(Comparator.comparing(Employee::getLastName)
                        .thenComparing(Employee::getFirstName)
                        .thenComparing(Employee::getSalary))
                .mapToInt(StreamsStuff::showEmpAndGetSalary)
                .sum(); //Calculate sum of salaries
        System.out.println("totalSalaries: " + totalSalaries);

        System.out.println("-------------------------------");

        peopleText.lines()
                .map(Employee::createEmployee)
                .map(e -> (Employee)e)
                .filter(Employee.dummySelector.negate())
                .map(Employee::getFirstName)
                .map(s -> s.split(""))
                .flatMap(Arrays::stream)
                .map(String::toLowerCase)
                .distinct()
                .forEach(System.out::print);
        System.out.println();

        System.out.println("-------------------------------");

        peopleText.lines()
                .filter(s -> s.contains("Cruise"))
                .forEach(System.out::println);

        System.out.println("-------------------------------");

        Optional<Employee> optionalEmployee = peopleText.lines()
                .map(Employee::createEmployee)
                .map(e -> (Employee)e)
                .filter(Employee.dummySelector.negate())
                .filter(e -> e.getSalary() < 0)
                .findFirst();
        System.out.println(optionalEmployee.map(Employee::getFirstName).orElse("Nobody"));

        System.out.println("-------------------------------");

        try {
            Files.lines(Path.of(filePath))
                    .map(Employee::createEmployee)
                    .filter(e -> ((Employee)e).getLastName().contains("Cruise"))
                    .forEach(System.out::println);
        } catch (IOException e) {
           e.printStackTrace();
        }

        System.out.println("-------------------------------");

        Collection<String> numSet = Set.of("one", "two", "three", "four");
        numSet.stream()
                .map(String::hashCode)
                .map(Integer::toHexString)
                .forEach(System.out::println);

        System.out.println("-------------------------------");
        record Car(String make, String model) {}
        Stream.of(new Car("Renault", "Twingo"), new Car("Peugeot", "308"), new Car("Opel", "GrandlandX"))
                .filter(c -> "Renault".equals(c.make))
                .forEach(System.out::println);

        System.out.println("-------------------------------");

        IntStream.rangeClosed(1,10)
                .mapToObj(String::valueOf)
                .map(s -> s.concat("-"))
                .forEach(System.out::print);
        System.out.println();

        System.out.println("-------------------------------");

        String[] strList = {"one", "two", "three", "four"};
        Arrays.stream(strList)
                .map(String::toUpperCase)
                .forEach(System.out::println);
    }
    private static int showEmpAndGetSalary(IEmployee iemployee) {
        System.out.println(iemployee);
        return iemployee.getSalary();
    }
}

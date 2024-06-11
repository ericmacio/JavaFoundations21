package com.eric.employees;

import java.util.*;
import java.util.regex.Matcher;

public class Main {
    private static final Set<IEmployee> employees = new TreeSet<>();
    private static final Map<String, Integer> salaryMap = new TreeMap<>();
    private static final String peopleText = """
                Cruise1, Fred, 1/1/1900, Programmer, {locpd=3000,yoe=10,iq=140}
                Cruise1, Fred1, 1/1/1900, Programmer, {locpd=3000,yoe=10,iq=140}
                Cruise2, Fred, 1/1/1900, Programmer, {locpd=4000,yoe=10,iq=140}
                Cruise3, Fred, 1/1/1900, Programmer, {locpd=5000,yoe=10,iq=140}
                Cruise4, Fred, 1/1/1900, Programmer, {locpd=6000,yoe=10,iq=140}
                Cruise5, Fred, 1/1/1900, Programmer, {locpd=7000,yoe=10,iq=140}
                Cruise1, Fred, 1/1/1900, Programmer, {locpd=3000,yoe=10,iq=140}
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
                BadEmployee, Wilma5, 3/3/1910, Analyt, {projectCount=9}
                Rubble1, Barney, 2/2/1905, Manager, {orgSize=300,dr=10}
                Rubble2, Barney2, 2/2/1905, Manager, {orgSize=100,dr=4}
                Rubble3, Barney3, 2/2/1905, Manager, {orgSize=200,dr=2}
                Rubble4, Barney4, 2/2/1905, Manager, {orgSize=500,dr=8}
                Rubble5, Barney5, 2/2/1905, Manager, {orgSize=175,dr=20}
                RubbleBad, Barney5, 2/2/1905, Manager, {orgSize=175,dr=20}
                """;


    public static void main(String[] args) {
        //Create employees list
        getEmployees();

        //Remove undesirable employees
        Set<String> removalNames = Set.of("CruiseBad", "RubbleBad", "SmithBad");
        //RemoveEmployee(removalNames);

        //Display employees
        DisplayEmployees();

        //Display salary
        String lastName = "Smith1";
        System.out.println("Salary of " + lastName + ": " + getSalary(lastName));
        System.out.println(salaryMap);
        System.out.println(salaryMap.values());

        //Test contains and equals
        //EvaluateContainsAndEquals(employees);
    }

    private static void getEmployees() {
        Matcher peopleMat = Employee.PEOPLE_PAT.matcher(peopleText);
        IEmployee iemployee = null;
        while (peopleMat.find()) {
            iemployee = Employee.createEmployee(peopleMat.group());
            Employee employee = (Employee)iemployee;
            employees.add(employee);
            salaryMap.put(employee.getLastName(), employee.getSalary());
        }
    }

    private static int getSalary(String lastName) {
        return salaryMap.get(lastName);
    }

    private static void EvaluateContainsAndEquals() {
        IEmployee myEmp = Employee.createEmployee("Cruise2, Fred, 1/1/1900, Programmer, {locpd=4000,yoe=10,iq=140}");
        System.out.println(employees.contains(myEmp));
        IEmployee myProgrammer = Employee.createEmployee("Cruise2, Fred, 1/1/1900, Programmer, {locpd=4000,yoe=10,iq=141}");
        System.out.println(employees.contains(myProgrammer));
    }

    private static void DisplayEmployees() {
        int totalSalaries = 0;
        for(IEmployee worker : employees) {
            System.out.println(worker.toString());
            totalSalaries += worker.getSalary();
        }
        System.out.println("Size: " + employees.size());
        System.out.println("totalSalaries: " + totalSalaries);
    }

    private static void RemoveEmployee(Set<String> removalNames) {
        for(Iterator<IEmployee> it = employees.iterator(); it.hasNext();) {
            IEmployee worker = it.next();
            if(worker instanceof Employee) {
                if (removalNames.contains(((Employee) worker).getLastName())) {
                    it.remove();
                }
            }
        }
    }
}

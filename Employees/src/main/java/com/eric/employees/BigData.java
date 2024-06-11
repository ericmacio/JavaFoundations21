package com.eric.employees;

import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class BigData {
    static final int SALARY_POS = 25;
    static final int FIRSTNAME_POS = 2;
    static final int LASTNAME_POS = 3;
    static final int STATE_POS = 32;
    static final int GENDER_POS = 5;
    static final String FILE_PATH = "/home/eric/Software/IdeaProjects/JavaFoundations21/Employees/src/main/java/com/eric/employees/people.csv";
    record Person (String firstName, String lastName, long salary, String state) {}
    record PersonGender (String firstName, String lastName, long salary, String state, char gender) {}
    public static void main(String[] args) {

        try {
            long startTimeMs = System.currentTimeMillis();
            long result = Files.lines(Path.of(FILE_PATH))
                    .skip(1)
                    .map(s -> s.split(","))
                    .map(a -> a[SALARY_POS])
                    .mapToLong(Long::parseLong)
                    .sum();
            long endTimeMs = System.currentTimeMillis();
            System.out.println(endTimeMs-startTimeMs + "ms");
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------------------------------");
        try {
            long startTimeMs = System.currentTimeMillis();
            long result = Files.lines(Path.of(FILE_PATH))
                    .skip(1)
                    .map(s -> s.split(","))
                    .map(a -> new Person(a[FIRSTNAME_POS], a[LASTNAME_POS], Long.parseLong(a[SALARY_POS]), a[STATE_POS]))
                    .mapToLong(Person::salary)
                    .sum();
            long endTimeMs = System.currentTimeMillis();
            System.out.println(endTimeMs-startTimeMs + "ms");
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------------------------------");
        try {
            long startTimeMs = System.currentTimeMillis();
            Map<String, List<Person>> result = Files.lines(Path.of(FILE_PATH))
                    .skip(1)
                    .limit(10)
                    .map(s -> s.split(","))
                    .map(a -> new Person(a[FIRSTNAME_POS], a[LASTNAME_POS], Long.parseLong(a[SALARY_POS]), a[STATE_POS]))
                    .collect(Collectors.groupingBy(Person::state, TreeMap::new, Collectors.toList()));
            long endTimeMs = System.currentTimeMillis();
            System.out.println(endTimeMs-startTimeMs + "ms");
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------------------------------");
        try {
            Files.lines(Path.of(FILE_PATH))
                    .skip(1)
                    .map(s -> s.split(","))
                    .map(a -> new Person(a[FIRSTNAME_POS], a[LASTNAME_POS], Long.parseLong(a[SALARY_POS]), a[STATE_POS]))
                    .collect(Collectors.groupingBy(
                                Person::state,
                                TreeMap::new,
                                Collectors.collectingAndThen(
                                        Collectors.summingLong(Person::salary),
                                        NumberFormat.getCurrencyInstance(Locale.US)::format)))
                    .forEach((state, salary) -> System.out.printf("%s -> %s%n", state, salary));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------------------------------");
        try {
            TreeMap<String, Map<Character, String>> result = Files.lines(Path.of(FILE_PATH))
                    .skip(1)
                    .map(s -> s.split(","))
                    .map(a -> new PersonGender(a[FIRSTNAME_POS], a[LASTNAME_POS], Long.parseLong(a[SALARY_POS]), a[STATE_POS], a[GENDER_POS].strip().charAt(0)))
                    .collect(Collectors.groupingBy(
                            PersonGender::state,
                            TreeMap::new,
                            Collectors.groupingBy(PersonGender::gender,
                                    Collectors.collectingAndThen(
                                            Collectors.averagingLong(PersonGender::salary),
                                            NumberFormat.getCurrencyInstance(Locale.US)::format))));
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------------------------------");
        try {
            Map<Boolean, Long> result = Files.lines(Path.of(FILE_PATH))
                    .skip(1)
                    .map(s -> s.split(","))
                    .map(a -> new PersonGender(a[FIRSTNAME_POS], a[LASTNAME_POS], Long.parseLong(a[SALARY_POS]), a[STATE_POS], a[GENDER_POS].strip().charAt(0)))
                    .collect(Collectors.partitioningBy(p -> p.gender() == 'F', counting()));
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

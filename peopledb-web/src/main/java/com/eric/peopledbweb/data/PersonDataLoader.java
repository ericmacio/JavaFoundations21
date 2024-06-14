package com.eric.peopledbweb.data;

import com.eric.peopledbweb.business.model.Person;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//@Component
public class PersonDataLoader implements ApplicationRunner {
    private PersonRepository personRepository;

    public PersonDataLoader(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (personRepository.count() == 0) {
            System.out.println("Running person data loader");
            List<Person> people = List.of(
                    new Person(
                            null,
                            "Pete",
                            "Smith1",
                            LocalDate.of(1980, 1, 1),
                            new BigDecimal("5000"),
                            "tom.jones@bbox.com",
                            null),
                    new Person(
                            null,
                            "Jennifer",
                            "Smith2",
                            LocalDate.of(1980, 1, 1),
                            new BigDecimal("5000"),
                            "tom.jones@bbox.com",
                            null),
                    new Person(
                            null,
                            "Rebecca",
                            "Smith3",
                            LocalDate.of(1980, 1, 1),
                            new BigDecimal("5000"),
                            "tom.jones@bbox.com",
                            null),
                    new Person(
                            null,
                            "Jo",
                            "Smith4",
                            LocalDate.of(1980, 1, 1),
                            new BigDecimal("5000"),
                            "tom.jones@bbox.com",
                            null),
                    new Person(
                            null,
                            "Bernie",
                            "Smith5",
                            LocalDate.of(1980, 1, 1),
                            new BigDecimal("5000"),
                            "tom.jones@bbox.com",
                            null)
            );
            personRepository.saveAll(people);
        }
    }
}

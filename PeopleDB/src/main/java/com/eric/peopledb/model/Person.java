package com.eric.peopledb.model;

import com.eric.peopledb.annotation.Id;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Person {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private ZonedDateTime dob;
    private BigDecimal salary = new BigDecimal("0");
    private String email;
    private Optional<Address> homeAddress = Optional.empty();
    private Optional<Address> businessAddress = Optional.empty();
    private Set<Person> children = new HashSet<>();
    private Optional<Person> parent = Optional.empty();

    public Person(String firstName, String lastName, ZonedDateTime dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public Person(String firstName, String lastName, ZonedDateTime dob, BigDecimal salary) {
        this(firstName, lastName, dob);
        this.salary = salary;
    }

    public Person(String firstName, String lastName, ZonedDateTime dob, BigDecimal salary, String email) {
        this(firstName, lastName, dob);
        this.email = email;
        this.salary = salary;
    }

    public Person(Long id, String firstName, String lastName, ZonedDateTime dob, BigDecimal salary, String email) {
        this(firstName, lastName, dob, salary, email);
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ZonedDateTime getDob() {
        return dob;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob=" + dob +
                ", salary=" + salary +
                ", email=" + email +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return salary == person.salary && Objects.equals(id, person.id) && firstName.equals(person.firstName) && lastName.equals(person.lastName)
                && dob.withZoneSameInstant(ZoneId.of("+0")).equals(person.dob.withZoneSameInstant(ZoneId.of("+0")));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, dob, salary);
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = Optional.ofNullable(homeAddress);
    }

    public Optional<Address> getHomeAddress() {
        return homeAddress;
    }

    public void setBusinessAddress(Address businessAddress) {
        this.businessAddress = Optional.ofNullable(businessAddress);
    }

    public Optional<Address> getBusinessAddress() {
        return businessAddress;
    }

    public void addChild(Person child) {
        children.add(child);
        child.setParent(this);
    }

    public void setParent(Person parent) {
        this.parent = Optional.ofNullable(parent);
    }

    public Optional<Person> getParent() {
        return parent;
    }

    public Set<Person> getChildren() {
        return children;
    }
}

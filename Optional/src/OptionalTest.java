import java.time.Year;
import java.util.Optional;

public class OptionalTest {
    record Car(String make, String model, String color, Year year) {}
    record Person(String firstName, String lastName, Car car) {}
    record PersonOptCar(String firstName, String lastName, Optional<Car> car) {}

    public static void main(String[] args) {
        String msg1 = "Hello";
        String msg2 = "notNull";
        Optional<String> optMsg2 = Optional.ofNullable(msg2);
        System.out.println(optMsg2.orElse("NULL"));
        System.out.println(optMsg2.filter(s -> s.length() > 8).orElse("Invalid"));
        String msg3 = null;
        Optional<String> optMsg3 = Optional.ofNullable(msg3);
        System.out.println(optMsg3.orElse("NULL"));
        if (optMsg3.isPresent()) {
            System.out.println(optMsg3.get());
        } else {
            System.out.println("Nothing in optMsg3");
        }
        //System.out.println(optMsg3.orElseThrow());
        System.out.println("-------------------");

        Person p1 = new Person("Tom", "Harry",
                new Car("Tesla", "X", "red", Year.of(2020)));
        Person p2 = new Person("Jerry", "Smith",
                new Car("Renault", "308", "blue", Year.of(2024)));
        Person p3 = null;
        Optional<Person> optPerson1 = Optional.of(p1);
        System.out.println(optPerson1);
        System.out.println(optPerson1.map(Person::firstName));
        Optional<Person> optPerson3 = Optional.ofNullable(p3);
        System.out.println(optPerson3.map(Person::firstName));
        System.out.println(optPerson3.map(Person::firstName).orElse("Unknown firstname"));
        System.out.println(optPerson3.map(Person::car).map(Car::make).orElse("Unknown make"));

        System.out.println("-------------------");

        PersonOptCar p4 = new PersonOptCar("Tom", "Harry",
                Optional.of(new Car("Tesla", "X", "red", Year.of(2020))));
        Optional<PersonOptCar> optPerson4 = Optional.of(p4);
        System.out.println(optPerson4
                .flatMap(PersonOptCar::car)
                .map(Car::make)
                .orElse("Make unknown again")
        );
        PersonOptCar p5 = new PersonOptCar("Tom", "Harry",
                Optional.ofNullable(null));
        Optional<PersonOptCar> optPerson5 = Optional.of(p5);
        System.out.println(optPerson5
                .flatMap(PersonOptCar::car)
                .map(Car::make)
                .orElse("Make unknown again")
        );

    }
}
package com.eric.peopledb.repository;

import com.eric.peopledb.model.Person;
import com.eric.peopledb.model.Region;
import com.eric.peopledb.model.Address;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PeopleRepositoryTest {
    private Connection connection;
    private PersonRepository repository;
    private static final String JDBC_URL = "jdbc:h2:/home/eric/Software/Databases/peopledb";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(JDBC_URL);
        connection.setAutoCommit(false);
        repository = new PersonRepository(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if(connection != null)
            connection.close();
    }

    @Test
    public void canSaveOnePerson() {
        Person john = new Person("Patricia", "Smith",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00));
        Person savedPerson = repository.save(john);
        System.out.println("savedPerson: " + savedPerson);
        assertThat(savedPerson.getId()).isGreaterThan(0);
    }

    @Test
    public void canSavePeopleWithHomeAddress() throws SQLException {
        Person rebecca = new Person("Rebecca", "Smith",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00), "rebecca.smith@gmail.com");
        Address address = new Address(null, "1 rue de comboire", "Maison 3", "Wala wala", "WA", "90210", "United States", "Fulton County", Region.WEST);
        rebecca.setHomeAddress(address);
        Person savedPerson = repository.save(rebecca);
        System.out.println("savedPerson: " + savedPerson);
        assertThat(savedPerson.getHomeAddress().get().id()).isGreaterThan(0);
    }

    @Test
    public void canSavePeopleWithBusinessAddress() throws SQLException {
        Person jo = new Person("Jo", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00), "rebecca.smith@gmail.com");
        Address address = new Address(null, "1 rue de comboire", "Maison 4", "Wala wala", "WA", "90210", "United States", "Fulton County", Region.WEST);
        jo.setBusinessAddress(address);
        Person savedPerson = repository.save(jo);
        System.out.println("savedPerson: " + savedPerson);
        assertThat(savedPerson.getBusinessAddress().get().id()).isGreaterThan(0);
    }

    @Test
    public void canSavePeopleWithChildren() throws SQLException {
        Person JoBar = new Person("JoBar", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00), "rebecca.smith@gmail.com");
        JoBar.addChild(new Person ("JoBarJunior1", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        JoBar.addChild(new Person ("JoBarJunior2", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        Person savedPerson = repository.save(JoBar);
        System.out.println("savedPerson: " + savedPerson);
        savedPerson.getChildren().stream()
                .map(Person::getId)
                .forEach(id -> assertThat(id).isGreaterThan(0));
    }
    
    @Test
    public void canSaveTwoPeople() {
        Person john = new Person("John", "Smith",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00));
        Person bobby = new Person("John", "Smith",
                ZonedDateTime.of(1990, 12, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00));
        Person savedPerson1 = repository.save(john);
        Person savedPerson2 = repository.save(bobby);
        assertThat(savedPerson1.getId()).isNotEqualTo(savedPerson2.getId());
    }
    
    @Test
    public void canFindPersonById() {
        ZonedDateTime dob = ZonedDateTime.now();
        Person savedPerson = repository.save(new Person("Bernard", "Apple", dob, BigDecimal.valueOf(2222)));
        Person foundPerson = repository.findById(savedPerson.getId()).get();
        assertThat(foundPerson.getId()).isEqualTo(savedPerson.getId());
    }

    @Test
    public void canFindPeopleByIdWithHomeAddress() throws SQLException {
        Person lola = new Person("lola", "Smith",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00), "rebecca.smith@gmail.com");
        Address address = new Address(null, "1 rue de comboire", "Maison 3", "Wala wala", "WA", "90210", "United States", "Fulton County", Region.WEST);
        lola.setHomeAddress(address);
        Person savedPerson = repository.save(lola);
        System.out.println("savedPerson: " + savedPerson);
        Person foundPerson = repository.findById(savedPerson.getId()).get();
        assertThat(foundPerson.getHomeAddress().get().state()).isEqualTo("WA");
    }

    @Test
    public void canFindPeopleByIdWithBusinessAddress() throws SQLException {
        Person lola = new Person("lola", "Smith",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00), "rebecca.smith@gmail.com");
        Address address = new Address(null, "1 rue de comboire", "Maison 5", "Wala wala", "AR", "90210", "United States", "Fulton County", Region.WEST);
        lola.setBusinessAddress(address);
        Person savedPerson = repository.save(lola);
        System.out.println("savedPerson: " + savedPerson);
        Person foundPerson = repository.findById(savedPerson.getId()).get();
        assertThat(foundPerson.getBusinessAddress().get().state()).isEqualTo("AR");
    }

    @Test
    public void canFindPeopleByIdWithChildren() throws SQLException {
        Person JoBarry = new Person("JoBarry", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6")), BigDecimal.valueOf(1000.00), "rebecca.smith@gmail.com");
        JoBarry.addChild(new Person ("JoBarryJunior1", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        JoBarry.addChild(new Person ("JoBarryJunior2", "Dalton",
                ZonedDateTime.of(1980, 11, 15, 15, 15, 0, 0, ZoneId.of("-6"))));
        Person savedPerson = repository.save(JoBarry);
        System.out.println("savedPerson: " + savedPerson);
        Person foundPerson = repository.findById(savedPerson.getId()).get();
        assertThat(foundPerson.getChildren().stream().map(Person::getFirstName).collect(Collectors.toSet())).contains("JoBarryJunior1", "JoBarryJunior2");
    }

    @Test
    public void testPersonIdNotFound() {
        Optional<Person> foundPerson = repository.findById(-1L);
        assertThat(foundPerson).isEmpty();
    }

    @Test
    public void canGetCount() {
        long startCount = repository.count();
        repository.save(new Person("John", "Berry",
                ZonedDateTime.of(1990, 12, 15, 15, 15, 0, 0, ZoneId.of("-6")),
                BigDecimal.valueOf(1000.00)));
        repository.save(new Person("John1", "Berry1",
                ZonedDateTime.of(1990, 12, 15, 15, 15, 0, 0, ZoneId.of("-6")),
                BigDecimal.valueOf(1000.00)));
        long endCount = repository.count();
        assertThat(endCount).isEqualTo(startCount+2);
    }

    @Test
    public void canDelete() {
        ZonedDateTime dob = ZonedDateTime.now();
        Person savedPerson = repository.save(new Person("Bob", "Alice", dob, BigDecimal.valueOf(1234)));
        long startCount = repository.count();
        repository.delete(savedPerson);
        long endCount = repository.count();
        assertThat(endCount).isEqualTo(startCount-1);
    }

    @Test
    public void canDeleteMultiplePeople() {
        Person p1 = repository.save(new Person("John", "Berry",
                ZonedDateTime.of(1990, 12, 15, 15, 15, 0, 0, ZoneId.of("-6")),
                BigDecimal.valueOf(1000.00)));
        Person p2 = repository.save(new Person("John1", "Berry1",
                ZonedDateTime.of(1990, 12, 15, 15, 15, 0, 0, ZoneId.of("-6")),
                BigDecimal.valueOf(1000.00)));
        long startCount = repository.count();
        repository.delete(p1, p2);
        long endCount = repository.count();
        assertThat(endCount).isEqualTo(startCount-2);
    }

    @Test
    public void canUpdate() {
        BigDecimal salaryInit = BigDecimal.valueOf(111);
        Person savedPerson = repository.save(new Person("Coco", "Macio",
                ZonedDateTime.of(1990, 12, 15, 15, 15, 0, 0, ZoneId.of("-6")),
                salaryInit));
        BigDecimal salaryModified = BigDecimal.valueOf(222);
        savedPerson.setSalary(salaryModified);
        repository.update(savedPerson);
        Person savedPersonUpdated = repository.findById(savedPerson.getId()).get();
        assertThat(savedPersonUpdated.getSalary()).isEqualTo(String.valueOf(salaryModified));
    }

    @Test
    public void canFindAll() {
        long count = repository.count();
        System.out.println("count: " + count);
        List<Person> personList = repository.findAll();
        assertThat(count).isEqualTo(personList.size());
    }

    @Test
    @Disabled
    public void loadData() throws IOException, SQLException {
        Files.lines(Path.of("/home/eric/Software/IdeaProjects/JavaFoundations21/Employees/src/main/java/com/eric/employees/people.csv"))
                .skip(1)
                .map(l -> l.split(","))
                .map(a -> {
                    LocalDate dob = LocalDate.parse(a[10], DateTimeFormatter.ofPattern("M/d/yyyy"));
                    LocalTime tob = LocalTime.parse(a[11], DateTimeFormatter.ofPattern("hh:mm:ss a"));
                    LocalDateTime dtob = LocalDateTime.of(dob, tob);
                    ZonedDateTime zdtob = ZonedDateTime.of(dtob, ZoneId.of("+0"));
                    Person person = new Person(a[2], a[4], zdtob, new BigDecimal(a[25]), a[6]);
                    return person;
                })
                .forEach(repository::save);
        connection.commit();
    }
}

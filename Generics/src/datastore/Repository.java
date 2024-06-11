package datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Repository<T extends Repository.IDable<V> & Repository.Saveable, V> {
    record Person(String firstName, String lastName, Long id) implements IDable<Long>, Saveable {}
    interface Saveable {}
    interface IDable<U> {
        U id();
    }
    private List<T> records = new ArrayList<>();

    List<T> findAll() {
        return records;
    }

    T save(T record) {
        records.add(record);
        return record;
    }

    T findById(long id) {
        return records.stream().filter(p -> p.id().equals(id)).findFirst().orElseThrow();
    }

    static <T,V> V encrypt(T data, Function<T,V> func) {
        return func.apply(data);
    }

    public static void main(String[] args) {
        Repository<Person, Long> pRepo = new Repository<>();
        pRepo.save(new Person("Tom", "Jerry", 10L));
        pRepo.save(new Person("John", "Travolta", 20L));
        System.out.println(pRepo.findAll());
        System.out.println(pRepo.findById(20L));

        System.out.println(Repository.<String, String>encrypt("Hello", String::toUpperCase));
        System.out.println(Repository.<String, Integer>encrypt("world", String::hashCode));
    }
}

package com.eric.peopledb.repository;

import com.eric.peopledb.annotation.Id;
import com.eric.peopledb.annotation.MultiSQL;
import com.eric.peopledb.annotation.SQL;
import com.eric.peopledb.exception.DataException;
import com.eric.peopledb.model.CrudOperation;
import come.eric.peopledb.exception.UnableToSaveException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class CrudRepository<T>  {

    protected Connection connection;
    private PreparedStatement savePS;
    private PreparedStatement findByIdPS;

    public CrudRepository(Connection connection) {
        try {
            this.connection = connection;
            savePS = connection.prepareStatement(getSqlByAnnotation(CrudOperation.SAVE, this::sqlNotDefined), Statement.RETURN_GENERATED_KEYS);
            findByIdPS = connection.prepareStatement(getSqlByAnnotation(CrudOperation.FIND_BY_ID, this::sqlNotDefined));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataException("Unable to create prepared statements for CrudRepository", e);
        }
    }

    private String getSqlByAnnotation(CrudOperation operationType, Supplier<String> sqlGetter) {
        Stream<SQL> multiSqlStream = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(MultiSQL.class))
                .map(m -> m.getAnnotation(MultiSQL.class))
                .flatMap(multisql -> Arrays.stream(multisql.value()));

        Stream<SQL> sqlStream =  Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(SQL.class))
                .map(m -> m.getAnnotation(SQL.class));

        return Stream.concat(multiSqlStream, sqlStream)
                .filter(a -> a.operationType().equals(operationType))
                .map(SQL::value)
                .findFirst()
                .orElseGet(sqlGetter);
    }

    private Long getIdByAnnotation(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .map(f -> {
                    f.setAccessible(true);
                    Long id = null;
                    try {
                        id = (long) f.get(entity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return id;
                })
                .findFirst().orElseThrow(() -> new RuntimeException("No Id annotated field found"));
    }
    
    private void setIdByAnnotation(Long id, T entity) {
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .forEach(f -> {
                    f.setAccessible(true);
                    try {
                        f.set(entity, id);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Unable to set ID field value.");
                    }
                });
    }

    public T save(T entity) {
        System.out.println("Save entity: " + entity);
        Long id = null;
        try {
            savePS.clearParameters();
            mapForSave(entity, savePS);
            int recordsAffected = savePS.executeUpdate();
            ResultSet rs = savePS.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getLong(1);
                System.out.println("Set entity id to: " + id);
                setIdByAnnotation(id, entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to save entity: " + entity);
        }
        postSave(entity, id);
        return entity;
    }

    public void update(T entity) {
        System.out.println("Update entity: " + entity);
        try {
            PreparedStatement ps = connection.prepareStatement(getSqlByAnnotation(CrudOperation.UPDATE, this::sqlNotDefined));
            mapForUpdate(entity, ps);
            int recordsAffected = ps.executeUpdate();
            System.out.printf("Records affected: %d%n", recordsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to update entity");
        }
    }

    public Optional<T> findById(Long id) {
        System.out.println("Find entity by id: " + id);
        T entity = null;
        try {
            findByIdPS.setLong(1, id);
            ResultSet rs = findByIdPS.executeQuery();
            while (rs.next()) {
                entity = getEntityFromResultSet(rs);
                System.out.println("Entity found: " + entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to find person by id: " + id);
        }
        return Optional.ofNullable(entity);
    }

    public List<T> findAll() {
        List<T> entityList = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(getSqlByAnnotation(CrudOperation.FIND_ALL, this::sqlNotDefined),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                T entity = getEntityFromResultSet(rs);
                entityList.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to find all entities");
        }
        entityList.forEach(System.out::println);
        return entityList;
    }

    public long count() {
        long count = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(getSqlByAnnotation(CrudOperation.COUNT, this::sqlNotDefined));
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                count = rs.getLong(1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void delete(T entity) {
        System.out.println("Delete entity: " + entity);
        try {
            PreparedStatement ps = connection.prepareStatement(getSqlByAnnotation(CrudOperation.DELETE_ONE, this::sqlNotDefined));
            ps.setLong(1, getIdByAnnotation(entity));
            int affectedRecords = ps.executeUpdate();
            System.out.println("Affected record: " + affectedRecords);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to delete person by id");
        }
    }

    public void delete(T... entities) {
        String ids = Arrays.stream(entities)
                .map(this::getIdByAnnotation)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        System.out.println("Delete multiple entities by ids: " + ids);
        try {
            Statement stmt = connection.createStatement();
            int affectedRecords = stmt.executeUpdate(getSqlByAnnotation(CrudOperation.DELETE_MANY, this::sqlNotDefined).replace(":ids", ids));
            System.out.println("Affected record: " + affectedRecords);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnableToSaveException("Tried to delete multiple people");
        }
    }

    private String sqlNotDefined() {
        throw new RuntimeException("SQL not defined");
    };

    protected void postSave(T entity, long id) {}

    /**
     *
     * @return Returns a String that represents the SQL needed to retrieve one entity
     * The SQL must contain one SQL parameter, i.e. "?", that will bind to the
     * entity's ID.
     */
    abstract void mapForSave(T entity, PreparedStatement ps) throws SQLException;

    abstract void mapForUpdate(T entity, PreparedStatement ps) throws SQLException;

    abstract T getEntityFromResultSet(ResultSet rs) throws SQLException;
}

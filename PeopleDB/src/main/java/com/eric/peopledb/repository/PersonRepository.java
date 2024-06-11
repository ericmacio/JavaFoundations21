package com.eric.peopledb.repository;

import com.eric.peopledb.annotation.SQL;
import com.eric.peopledb.model.Address;
import com.eric.peopledb.model.CrudOperation;
import com.eric.peopledb.model.Person;
import com.eric.peopledb.model.Region;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class PersonRepository extends CrudRepository<Person> {

    private AddressRepository addressRepository = null;
    private Map<String, Integer> aliasColIdxMap = new HashMap<>();
    public static final String SAVE_PERSON_SQL = """
        INSERT INTO PEOPLE 
        (FIRST_NAME, LAST_NAME, DOB, SALARY, EMAIL, HOME_ADDRESS, BUSINESS_ADDRESS, PARENT_ID) 
        VALUES(?, ?, ?, ?, ?, ?, ?, ?)
        """;
    //SELECT P.ID, P.FIRST_NAME, P.LAST_NAME, P.DOB, P.SALARY, P.EMAIL, P.HOME_ADDRESS, A.ID, A.STREET_ADDRESS, A.ADDRESS2, A.CITY, A.STATE, A.POSTCODE, A.COUNTRY, A.COUNTY, A.REGION FROM PEOPLE AS P LEFT OUTER JOIN ADDRESSES AS A ON P.HOME_ADDRESS = A.ID WHERE P.ID = 1
    public static final String OLD_FIND_BY_ID_SQL = """
        SELECT
        P.ID, P.FIRST_NAME, P.LAST_NAME, P.DOB, P.SALARY, P.EMAIL, P.HOME_ADDRESS, P.BUSINESS_ADDRESS,
        HOME.ID AS HOME_ID, HOME.STREET_ADDRESS AS HOME_STREET_ADDRESS, HOME.ADDRESS2 AS HOME_ADDRESS2, HOME.CITY AS HOME_CITY, HOME.STATE AS HOME_STATE, HOME.POSTCODE AS HOME_POSTCODE, HOME.COUNTRY AS HOME_COUNTRY, HOME.COUNTY AS HOME_COUNTY, HOME.REGION AS HOME_REGION,
        BUSINESS.ID AS BUSINESS_ID, BUSINESS.STREET_ADDRESS AS BUSINESS_STREET_ADDRESS, BUSINESS.ADDRESS2 AS BUSINESS_ADDRESS2, BUSINESS.CITY AS BUSINESS_CITY, BUSINESS.STATE AS BUSINESS_STATE, BUSINESS.POSTCODE AS BUSINESS_POSTCODE, BUSINESS.COUNTRY AS BUSINESS_COUNTRY, BUSINESS.COUNTY AS BUSINESS_COUNTY, BUSINESS.REGION AS BUSINESS_REGION
        FROM PEOPLE AS P
        LEFT OUTER JOIN ADDRESSES AS HOME ON P.HOME_ADDRESS = HOME.ID
        LEFT OUTER JOIN ADDRESSES AS BUSINESS ON P.BUSINESS_ADDRESS = BUSINESS.ID
        WHERE P.ID = ?
        """;
    public static final String FIND_BY_ID_SQL = """
        SELECT
        PARENT.ID AS PARENT_ID, PARENT.FIRST_NAME AS PARENT_FIRST_NAME, PARENT.LAST_NAME AS PARENT_LAST_NAME, PARENT.DOB AS PARENT_DOB, PARENT.SALARY AS PARENT_SALARY, PARENT.EMAIL AS PARENT_EMAIL,
        CHILD.ID AS CHILD_ID, CHILD.FIRST_NAME AS CHILD_FIRST_NAME, CHILD.LAST_NAME AS CHILD_LAST_NAME, CHILD.DOB AS CHILD_DOB, CHILD.SALARY AS CHILD_SALARY, CHILD.EMAIL AS CHILD_EMAIL,
        HOME.ID AS HOME_ID, HOME.STREET_ADDRESS AS HOME_STREET_ADDRESS, HOME.ADDRESS2 AS HOME_ADDRESS2, HOME.CITY AS HOME_CITY, HOME.STATE AS HOME_STATE, HOME.POSTCODE AS HOME_POSTCODE, HOME.COUNTRY AS HOME_COUNTRY, HOME.COUNTY AS HOME_COUNTY, HOME.REGION AS HOME_REGION,
        BUSINESS.ID AS BUSINESS_ID, BUSINESS.STREET_ADDRESS AS BUSINESS_STREET_ADDRESS, BUSINESS.ADDRESS2 AS BUSINESS_ADDRESS2, BUSINESS.CITY AS BUSINESS_CITY, BUSINESS.STATE AS BUSINESS_STATE, BUSINESS.POSTCODE AS BUSINESS_POSTCODE, BUSINESS.COUNTRY AS BUSINESS_COUNTRY, BUSINESS.COUNTY AS BUSINESS_COUNTY, BUSINESS.REGION AS BUSINESS_REGION
        FROM PEOPLE AS PARENT
        LEFT OUTER JOIN PEOPLE AS CHILD ON PARENT.ID = CHILD.PARENT_ID
        LEFT OUTER JOIN ADDRESSES AS HOME ON PARENT.HOME_ADDRESS = HOME.ID
        LEFT OUTER JOIN ADDRESSES AS BUSINESS ON PARENT.BUSINESS_ADDRESS = BUSINESS.ID
        WHERE PARENT.ID = ?
        """;
    public static final String COUNT_PERSON_SQL = "SELECT COUNT(*) FROM PEOPLE";
    public static final String DELETE_PERSON_BY_ID_SQL = "DELETE FROM PEOPLE WHERE ID = ?";
    public static final String DELETE_MULTIPLE_PERSON_SQL = "DELETE FROM PEOPLE WHERE ID IN (:ids)";
    public static final String UPDATE_PERSON_SQL = "UPDATE PEOPLE SET FIRST_NAME=?, LAST_NAME=?, DOB=?, SALARY=? WHERE ID = ?";
    private static final String FIND_ALL_SQL = """
        SELECT
        PARENT.ID AS PARENT_ID, PARENT.FIRST_NAME AS PARENT_FIRST_NAME, PARENT.LAST_NAME AS PARENT_LAST_NAME, PARENT.DOB AS PARENT_DOB, PARENT.SALARY AS PARENT_SALARY, PARENT.EMAIL AS PARENT_EMAIL,
        FROM PEOPLE AS PARENT
        """;
    //CREATE TABLE PEOPLE(ID BIGINT AUTO_INCREMENT, FIRST_NAME VARCHAR(255), LAST_NAME VARCHAR(255), DOB TIMESTAMP, SALARY DECIMAL);
    //INSERT INTO PEOPLE (FIRST_NAME, LAST_NAME, DOB, SALARY) VALUES('Harry', 'Johson', '1950-03-15 10:45:10', 100000.00);

    public PersonRepository(Connection connection) {
        super(connection);
        addressRepository = new AddressRepository(connection);
    }

    @Override
    @SQL(value = SAVE_PERSON_SQL, operationType = CrudOperation.SAVE)
    void mapForSave(Person entity, PreparedStatement ps) throws SQLException {
        Address savedAddress;
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, Timestamp.valueOf(entity.getDob().withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime()));
        ps.setBigDecimal(4, entity.getSalary());
        ps.setString(5, entity.getEmail());
        associateAddressWithPerson(ps, entity.getHomeAddress(), 6);
        associateAddressWithPerson(ps, entity.getBusinessAddress(), 7);
        associateChildWithPerson(entity, ps);
    }

    private static void associateChildWithPerson(Person entity, PreparedStatement ps) throws SQLException {
        Optional<Person> parent = entity.getParent();
        if(parent.isPresent()) {
            ps.setLong(8, parent.get().getId());
        } else {
            ps.setObject(8, null);
        }
    }

    @Override
    protected void postSave(Person entity, long id) {
        entity.getChildren().stream()
                .forEach(this::save);
    }

    private void associateAddressWithPerson(PreparedStatement ps, Optional<Address> address, int index) throws SQLException {
        Address savedAddress;
        if (address.isPresent()) {
            savedAddress = addressRepository.save(address.get());
            ps.setLong(index, savedAddress.id());
        } else {
            ps.setObject(index, null);
        }
    }

    @Override
    @SQL(value = UPDATE_PERSON_SQL, operationType = CrudOperation.UPDATE)
    void mapForUpdate(Person entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFirstName());
        ps.setString(2, entity.getLastName());
        ps.setTimestamp(3, Timestamp.valueOf(entity.getDob().withZoneSameInstant(ZoneId.of("+0")).toLocalDateTime()));
        ps.setBigDecimal(4, entity.getSalary());
        ps.setLong(5, entity.getId());
    }

    @Override
    @SQL(value = FIND_BY_ID_SQL, operationType = CrudOperation.FIND_BY_ID)
    @SQL(value = FIND_ALL_SQL, operationType = CrudOperation.FIND_ALL)
    @SQL(value = COUNT_PERSON_SQL, operationType = CrudOperation.COUNT)
    @SQL(value = DELETE_PERSON_BY_ID_SQL, operationType = CrudOperation.DELETE_ONE)
    @SQL(value = DELETE_MULTIPLE_PERSON_SQL, operationType = CrudOperation.DELETE_MANY)
    Person getEntityFromResultSet(ResultSet rs) throws SQLException {
        Person finalParent = null;
        do {
            Person currentParent = extractPerson(rs, "PARENT_").get();
            if(finalParent == null) {
                finalParent = currentParent;
            }
            if(!finalParent.equals(currentParent)) {
                rs.previous();
                break;
            }
            Optional<Person> child = extractPerson(rs, "CHILD_");
            Address homeAddress = extractAddress(rs, "HOME_");
            Address businessAddress = extractAddress(rs, "BUSINESS_");
            finalParent.setHomeAddress(homeAddress);
            finalParent.setBusinessAddress(businessAddress);
            child.ifPresent(finalParent::addChild);
        } while (rs.next());
        return finalParent;
    }

    private Optional<Person> extractPerson(ResultSet rs, String aliasPrefix) throws SQLException {
        Long personId = getValueByAlias(aliasPrefix + "ID", rs, Long.class);
        if(personId == null) { return Optional.empty(); }
        String firstName = getValueByAlias(aliasPrefix + "FIRST_NAME", rs, String.class);
        String lastName = getValueByAlias(aliasPrefix + "LAST_NAME", rs, String.class);
        ZonedDateTime dob = ZonedDateTime.of(getValueByAlias(aliasPrefix + "DOB", rs, Timestamp.class).toLocalDateTime(), ZoneId.of("+0"));
        BigDecimal salary = getValueByAlias(aliasPrefix + "SALARY", rs, BigDecimal.class);
        String email = getValueByAlias(aliasPrefix + "EMAIL", rs, String.class);
        return Optional.of(new Person(personId, firstName, lastName, dob, salary, email));
    }

    private Address extractAddress(ResultSet rs, String aliasPrefix) throws SQLException {
        Long addressId = getValueByAlias(aliasPrefix + "ID", rs, Long.class);
        if(addressId == null) return null;
        String streetAddress = getValueByAlias(aliasPrefix + "STREET_ADDRESS", rs, String.class);
        String address2 = getValueByAlias(aliasPrefix + "ADDRESS2", rs, String.class);
        String city = getValueByAlias(aliasPrefix + "CITY", rs, String.class);
        String state = getValueByAlias(aliasPrefix + "STATE", rs, String.class);
        String postcode = getValueByAlias(aliasPrefix + "POSTCODE", rs, String.class);
        String county = getValueByAlias(aliasPrefix + "COUNTY", rs, String.class);
        String country = getValueByAlias(aliasPrefix + "COUNTRY", rs, String.class);
        Region region = Region.valueOf(Objects.requireNonNull(getValueByAlias(aliasPrefix + "REGION", rs, String.class)).toUpperCase());
        return new Address(addressId, streetAddress, address2, city, state, postcode, country, county, region);
    }

    private <T> T getValueByAlias(String alias, ResultSet rs, Class<T> clazz) throws SQLException {
        int columnCount = rs.getMetaData().getColumnCount();
        int foundIdx = getIndexForAlias(alias, rs, columnCount);
        return (foundIdx == 0) ? null : (T) rs.getObject(foundIdx);
        //throw new SQLException(String.format("Column not found for alias: '%s'", alias));
    }

    private int getIndexForAlias(String alias, ResultSet rs, int columnCount) throws SQLException {
        Integer foundIdx = aliasColIdxMap.getOrDefault(alias, 0);
        if (foundIdx == 0) {
            for (int colIdx = 1; colIdx <= columnCount; colIdx++) {
                if (alias.equals(rs.getMetaData().getColumnLabel(colIdx))) {
                    foundIdx = colIdx;
                    aliasColIdxMap.put(alias, foundIdx);
                    break;
                }
            }
        }
        return foundIdx;
    }
}

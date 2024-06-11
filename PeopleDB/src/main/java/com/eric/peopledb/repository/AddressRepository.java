package com.eric.peopledb.repository;

import com.eric.peopledb.annotation.SQL;
import com.eric.peopledb.model.Address;
import com.eric.peopledb.model.CrudOperation;
import com.eric.peopledb.model.Region;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRepository extends CrudRepository<Address> {

    public static final String FIND_BY_ID_SQL = """
        SELECT ID, STREET_ADDRESS, ADDRESS2, CITY, STATE, POSTCODE, COUNTY, REGION, COUNTRY
        FROM ADDRESSES
        WHERE ID = ?
        """;
    public AddressRepository(Connection connection) {
        super(connection);
    }

    @Override
    @SQL(operationType= CrudOperation.SAVE, value = """
        INSERT INTO ADDRESSES (STREET_ADDRESS, ADDRESS2, CITY, STATE, POSTCODE, COUNTRY, COUNTY, REGION)
        VALUES(?,?,?,?,?,?,?,?)
        """)
    void mapForSave(Address entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.streetAddress());
        ps.setString(2, entity.address2());
        ps.setString(3, entity.city());
        ps.setString(4, entity.state());
        ps.setString(5, entity.postcode());
        ps.setString(6, entity.country());
        ps.setString(7, entity.county());
        ps.setString(8, entity.region().toString());
    }

    @Override
    void mapForUpdate(Address entity, PreparedStatement ps) throws SQLException {

    }

    @Override
    @SQL(value = FIND_BY_ID_SQL, operationType = CrudOperation.FIND_BY_ID)
    Address getEntityFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("ID");
        String streetAddress = rs.getString("STREET_ADDRESS");
        String address2 = rs.getString("ADDRESS2");
        String city = rs.getString("CITY");
        String state = rs.getString("STATE");
        String postcode = rs.getString("POSTCODE");
        String county = rs.getString("COUNTY");
        String country = rs.getString("COUNTRY");
        Region region = Region.valueOf(rs.getString("REGION").toUpperCase());
        Address address = new Address(id, streetAddress, address2, city, state, postcode, country, county, region);
        return address;
    }
}

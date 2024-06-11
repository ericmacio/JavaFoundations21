package com.eric.peopledb.exception;

import java.sql.SQLException;

public class DataException extends RuntimeException {
    public DataException(String msg) {
        super(msg);
    }
    public DataException(String msg, SQLException e) {
        super(msg, e);
    }
}

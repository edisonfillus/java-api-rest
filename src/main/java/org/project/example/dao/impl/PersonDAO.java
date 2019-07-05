package org.project.example.dao.impl;

import java.sql.SQLException;

import org.project.example.dao.DAOConnectionManager;

public class PersonDAO {

    DAOConnectionManager connection;

    public PersonDAO(DAOConnectionManager connection) {
        this.connection = connection;
    }

    // TODO: Just to compile
    public int count() throws SQLException {
        connection.prepareStatement("sql");
        return 1;   
    }

    
    
}
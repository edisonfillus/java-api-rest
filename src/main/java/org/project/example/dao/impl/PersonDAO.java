package org.project.example.dao.impl;

import java.sql.SQLException;
import org.project.example.dao.DAOManager;

public class PersonDAO {

    DAOManager manager;

    public PersonDAO(DAOManager manager) {
        this.manager = manager;
    }

    // TODO: Just to compile
    public int count() throws SQLException {
        manager.getConnection().clearWarnings();
        return 1;   
    }

    
    
}
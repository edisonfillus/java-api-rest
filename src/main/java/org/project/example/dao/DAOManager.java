package org.project.example.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
 
import org.project.example.dao.impl.PersonDAO;
 
public class DAOManager {
    //http://tutorials.jenkov.com/java-persistence/dao-manager.html
    private static DataSource dataSource = null; // Singleton Datasource
    protected Connection connection = null; // Instance connection
    protected PersonDAO personDAO = null; // Instance DAO

    public Connection getConnection() throws SQLException {
        if (this.connection == null) {
            this.connection = dataSource.getConnection();
            this.connection.setAutoCommit(false);
        }
        return this.connection;
    }

    public PersonDAO getPersonDao() throws SQLException {
        if (this.personDAO == null) {
            this.personDAO = new PersonDAO(this);
        }
        return this.personDAO;
    }


}
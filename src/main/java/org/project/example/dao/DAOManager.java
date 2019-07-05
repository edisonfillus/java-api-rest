package org.project.example.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
 
import org.project.example.dao.impl.PersonDAO;
 
public class DAOManager {

    //http://tutorials.jenkov.com/java-persistence/dao-manager.html
    private static DataSource dataSource = null; // Singleton Datasource
    private Connection connection = null; // Instance connection
    private DAOConnectionManager connectionManager; // Wrapper for connection
    
    // Available DAOs
    private PersonDAO personDAO = null; // Instance DAO

    /**
     * Lazy load JDBC Connection
     * @return
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        if (this.connection == null) {
            this.connection = dataSource.getConnection();
            this.connection.setAutoCommit(false);
        }
        return this.connection;
    }

    /**
     * Lazy load Connection Wrapper
     * @return
     * @throws SQLException
     */
    private DAOConnectionManager getConnectionManager() throws SQLException {
        if(this.connectionManager == null){
            this.connectionManager = new DAOConnectionManager(getConnection());
        }
        return this.connectionManager;
    }


    /**
     * Lazy load Person DAO
     */
    public PersonDAO getPersonDao() throws SQLException {
        if (this.personDAO == null) {
            this.personDAO = new PersonDAO(getConnectionManager());
        }
        return this.personDAO;
    }


}
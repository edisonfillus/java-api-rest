package org.project.example.data;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.project.example.data.impl.PersonDAO;

public class DAOManager implements AutoCloseable {

    private Connection connection = null; // Instance connection
    private DAOConnectionManager connectionManager = null; // Wrapper for connection
    // Available DAOs (Only one instance per DAOManager)
    private PersonDAO personDAO = null;

    private DAOManager(Connection connection) {
        this.connection = connection;
        this.connectionManager = new DAOConnectionManager(connection);
    }

    /**
     * Lazy load Person DAO
     * 
     * @throws IOException
     */
    public PersonDAO getPersonDao() throws SQLException {
        if (this.personDAO == null) {
            this.personDAO = new PersonDAO(connectionManager);
        }
        return this.personDAO;
    }

    public DAOConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    public void commitTransaction() throws SQLException {
        connection.commit();
    }

    public void rollbackTransaction() throws SQLException {
        connection.rollback();
    }

    public static class Builder {

        private boolean autoCommit = true;

        private Connection getConnection() throws SQLException {
            Connection conn = null;
            try {
                String DATASOURCE_CONTEXT = "java:/example-ds";
                Context initialContext = new InitialContext();
                DataSource datasource = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);
                conn = datasource.getConnection();
            } catch (NamingException ex) {
                throw new SQLException(ex.getMessage(),ex);
            }
            return conn;
        }

        public Builder() {
        }

        public Builder disableAutoCommit() {
            this.autoCommit = false;
            return this;
        }

        public DAOManager build() throws SQLException {
            Connection conn = getConnection();
            conn.setAutoCommit(this.autoCommit);
            return new DAOManager(conn);

        }
    }

}
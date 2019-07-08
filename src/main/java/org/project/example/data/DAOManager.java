package org.project.example.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
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
        private static DataSource dataSource = null; // Datasource singleton
        private static final String PROPERTIES_FILE_NAME = "persistence.properties";

        private DataSource getDataSource() throws SQLException {
            if (dataSource == null) {
                InputStream inputStream = DAOManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
                Properties prop = new Properties();
                if (inputStream == null) {
                    throw new SQLException(new FileNotFoundException(
                            "property file '" + PROPERTIES_FILE_NAME + "' not found in the classpath"));
                }
                try {
                    prop.load(inputStream);
                } catch (IOException e) {
                    throw new SQLException(e);
                }
                JdbcDataSource ds = new JdbcDataSource();
                ds.setURL(prop.getProperty("jdbc.url"));
                return ds;

            }
            return dataSource;
        }

        public Builder() {
        }

        public Builder disableAutoCommit() {
            this.autoCommit = false;
            return this;
        }

        public DAOManager build() throws SQLException {
            Connection conn = getDataSource().getConnection();
            conn.setAutoCommit(autoCommit);
            return new DAOManager(conn);

        }
    }

 

}
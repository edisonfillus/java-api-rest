package org.project.example.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 * DAOFactory
 */
public class DAOFactory {

    // http://tutorials.jenkov.com/java-persistence/dao-manager.html
    private static DataSource dataSource = null; // Datasource singleton
    private static final String PROPERTIES_FILE_NAME = "persistence.properties";

    protected static DataSource getDataSource() throws SQLException {
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

    //public static DAOManager createDAOManager(boolean autoCommit) throws SQLException {
    //    Connection conn = getDataSource().getConnection();
    //    conn.setAutoCommit(autoCommit);
    //    return new DAOManager(conn);
    //}

}
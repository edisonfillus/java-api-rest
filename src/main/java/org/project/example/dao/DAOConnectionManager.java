package org.project.example.dao;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Class to encapsulate a Connection and only provide access do DAOs permitted operations 
 */
public class DAOConnectionManager {

    private Connection connection;

    public DAOConnectionManager(Connection connection) {
        this.connection = connection;
    }

  
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

}
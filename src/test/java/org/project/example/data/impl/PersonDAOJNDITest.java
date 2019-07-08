package org.project.example.data.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.project.example.server.JettyIntegrationTest;

public class PersonDAOJNDITest {

    private static Logger log = LogManager.getLogger(PersonDAOJNDITest.class);

    @BeforeAll
    public static void setUp() {
        try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:/comp");
            ic.createSubcontext("java:/comp/env");
            ic.createSubcontext("java:/comp/env/jdbc");

            JdbcConnectionPool ds = JdbcConnectionPool.create(
                    "jdbc:h2:file:./src/test/resources/test.db;FILE_LOCK=NO;MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE", "sa", "sa");
            // Construct DataSource
            // OracleConnectionPoolDataSource ds = new
            // OracleConnectionPoolDataSource();
            // ds.setURL("jdbc:oracle:thin:@host:port:db");
            // ds.setUser("MY_USER_NAME");
            // ds.setPassword("MY_USER_PASSWORD");

            ic.bind("java:/mydatasourcename", ds);
        } catch (NamingException e) {
            log.error(e, e);
        }

    }

    @Test
    public void testSimple() throws Exception {

        // Obtain our environment naming context
        Context initCtx = new InitialContext();

        // Look up our datasource
        DataSource ds = (DataSource) initCtx.lookup("java:/mydatasourcename");

        Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();

        ResultSet rset = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");

        while (rset.next()) {
            System.out.println("<<<\t" + rset.getString("TABLE_NAME"));
        }

    }

}
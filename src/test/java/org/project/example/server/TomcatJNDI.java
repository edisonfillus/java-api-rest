package org.project.example.server;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class TomcatJNDI {

    public void registerDS(DataSource ds, String name) throws NamingException {

        // Create initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");

        // Construct DataSource
        // OracleConnectionPoolDataSource ds = new
        // OracleConnectionPoolDataSource();
        // ds.setURL("jdbc:oracle:thin:@host:port:db");
        // ds.setUser("MY_USER_NAME");
        // ds.setPassword("MY_USER_PASSWORD");

        ic.bind("java:/" + name, ds);

    }
}
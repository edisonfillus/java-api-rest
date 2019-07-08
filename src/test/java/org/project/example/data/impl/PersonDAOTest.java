package org.project.example.data.impl;


import java.sql.SQLException;
import javax.naming.NamingException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.project.example.data.DAOConnectionManager;
import org.project.example.data.DAOManager;
import org.project.example.model.Person;
import org.project.example.server.TomcatJNDI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(OrderAnnotation.class)
public class PersonDAOTest {

private static Logger log = LogManager.getLogger(PersonDAOTest.class);

    @BeforeAll
    public static void initDB() throws SQLException, NamingException {
        // Register the Pool on JNDI and 
        try {
            //JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:file:./src/test/resources/test.db;FILE_LOCK=NO;MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE", "sa", "sa");
            JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'src/test/resources/init.sql';TRACE_LEVEL_SYSTEM_OUT=2", "sa", "sa");
            new TomcatJNDI().registerDS(ds, "example-ds");
        } catch (NamingException e) {
            log.error(e,e);
            throw e;
        }
                
        // Truncate Database
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            DAOConnectionManager conn = daoManager.getConnectionManager();
            conn.createStatement().executeUpdate("TRUNCATE TABLE PERSON");
        } catch (SQLException e) {
            log.error(e,e);
            throw e;
        }
    } 

    @Test
    @Order(1)
    public void testCreate() throws SQLException {
        Person person = new Person();
        person.setName("Test");

        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            person = daoManager.getPersonDao().createPerson(person);
        }
        assertThat(person.getId(),is(1l));
    }

    @Test
    @Order(2)
    public void testFindPresent() throws SQLException {
        Person person = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            person = daoManager.getPersonDao().findPerson(1l);
        }
        assertThat(person,notNullValue());
        assertThat(person.getName(),is("Test"));
    }

    @Test
    @Order(3)
    public void testFindNotExistent() throws SQLException {
        Person person = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            person = daoManager.getPersonDao().findPerson(99999l);
        }
        assertThat(person,nullValue());
    }

}
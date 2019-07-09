package org.project.example.data.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.naming.NamingException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.Assertions;
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
            // JdbcConnectionPool ds =
            // JdbcConnectionPool.create("jdbc:h2:file:./src/test/resources/test.db;FILE_LOCK=NO;MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE",
            // "sa", "sa");
            JdbcConnectionPool ds = JdbcConnectionPool.create(
                    "jdbc:h2:mem:example;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'src/test/resources/init.sql';TRACE_LEVEL_SYSTEM_OUT=2",
                    "sa", "sa");
            new TomcatJNDI().registerDS(ds, "example-ds");
        } catch (NamingException e) {
            log.error(e, e);
            throw e;
        }

        // Truncate Database
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            DAOConnectionManager conn = daoManager.getConnectionManager();
            conn.createStatement().executeUpdate("TRUNCATE TABLE person");
        } catch (SQLException e) {
            log.error(e, e);
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
        assertThat(person.getId(), is(1l));
    }

    @Test
    @Order(2)
    public void testFindPresent() throws SQLException {
        Person person = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            person = daoManager.getPersonDao().findPersonById(1l);
        }
        assertThat(person, notNullValue());
        assertThat(person.getName(), is("Test"));
    }

    @Test
    @Order(3)
    public void testFindNotExistent() throws SQLException {
        Person person = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            person = daoManager.getPersonDao().findPersonById(99999l);
        }
        assertThat(person, nullValue());
    }

    @Test
    @Order(4)
    public void testListAll() throws SQLException {
        List<Person> persons = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            persons = daoManager.getPersonDao().listAllPerson();
        }
        assertThat(persons, notNullValue());
        assertThat(persons, hasSize(1));
        assertThat(persons.get(0).getName(), is("Test"));
    }

    @Test
    @Order(5)
    public void testUpdate() throws SQLException {
        Person personToUpdate = new Person();
        personToUpdate.setId(1l);
        personToUpdate.setName("TestUpdate");
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            daoManager.getPersonDao().updatePerson(personToUpdate);
        }
        Person personUpdated = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            personUpdated = daoManager.getPersonDao().findPersonById(1);
        }

        assertThat(personUpdated, notNullValue());
        assertThat(personUpdated.getName(), is("TestUpdate"));
    }

    @Test
    @Order(6)
    public void testUpdateNonExistent() throws SQLException {
        Person personToUpdate = new Person();
        personToUpdate.setId(9999l);
        personToUpdate.setName("TestUpdate");
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            try (DAOManager daoManager = new DAOManager.Builder().build()) {
                daoManager.getPersonDao().updatePerson(personToUpdate);
            }
        });
    }

    @Test
    @Order(7)
    public void testDelete() throws SQLException {
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            daoManager.getPersonDao().deletePersonById(1l);
        }
        Person personDeleted = null;
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            personDeleted = daoManager.getPersonDao().findPersonById(1l);
        }
        assertThat(personDeleted, nullValue());
    }

    @Test
    @Order(8)
    public void testDeleteNonExistent() throws SQLException {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            try (DAOManager daoManager = new DAOManager.Builder().build()) {
                daoManager.getPersonDao().deletePersonById(999999l);
            }
        });
    }
}
package org.project.example.data.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.project.example.data.DAOConnectionManager;
import org.project.example.data.DAOManager;
import org.project.example.model.Person;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(OrderAnnotation.class)
public class PersonDAOTest {

private static Logger log = LogManager.getLogger(PersonDAOTest.class);

    @BeforeAll
    public static void initDB() throws SQLException {
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            DAOConnectionManager conn = daoManager.getConnectionManager();
            conn.createStatement().executeUpdate("TRUNCATE TABLE PERSON");
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

}
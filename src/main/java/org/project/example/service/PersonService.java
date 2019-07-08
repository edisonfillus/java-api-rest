package org.project.example.service;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.project.example.data.DAOFactory;
import org.project.example.data.DAOManager;
import org.project.example.model.Person;

public class PersonService {

    public Person create(Person person) throws SQLException {
        try (DAOManager daoManager = new DAOManager.Builder().build()) {
            //try {
                person = daoManager.getPersonDao().createPerson(person);
            //    daoManager.commitTransaction();
            //} catch (SQLException e) {
            //    daoManager.rollbackTransaction();
            //    throw e;
            //}
        }
        return person;
    }

}

package org.project.example.data.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.project.example.data.DAOConnectionManager;
import org.project.example.model.Person;

public class PersonDAO {

    private DAOConnectionManager connection;

    public PersonDAO(DAOConnectionManager connection) {
        this.connection = connection;
    }

    /**
     * 
     * @param person Person to create
     * @return Person created with id
     * @throws SQLException
     */
    public Person createPerson(Person person) throws SQLException {
        String sql = new StringBuilder()
            .append("INSERT INTO PERSON (NAME)")
            .append("VALUES (?);").toString();

        // prepared statement para inserção
        PreparedStatement stmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        // seta os valores
        stmt.setString(1, person.getName());
        int affectedRows = stmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating person failed, no rows affected.");
        }

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                person.setId(generatedKeys.getLong(1));
            }
            else {
                throw new SQLException("Creating person failed, no ID obtained.");
            }
        }

        return person;
    }

}
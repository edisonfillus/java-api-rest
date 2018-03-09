package org.project.example.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Notification {

    private Integer id;
    private String message;

    /**
     * Default no-args constructor needed for jaxb
     */
    public Notification() {
    }

    public Notification(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
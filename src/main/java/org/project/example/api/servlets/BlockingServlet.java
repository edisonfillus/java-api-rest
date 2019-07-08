package org.project.example.api.servlets;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

//@WebServlet(name = "BlockingServlet", urlPatterns = "/blocking")
public class BlockingServlet extends HttpServlet {

	private static final long serialVersionUID = 8425478659027973920L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
  
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setStatus(HttpServletResponse.SC_OK);
        
        JsonObject content = Json.createObjectBuilder()
        .add("status", "blocking")
        .build();

        response.getWriter().println(content.toString());
    }
}
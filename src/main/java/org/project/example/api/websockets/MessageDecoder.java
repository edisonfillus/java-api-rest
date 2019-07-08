package org.project.example.api.websockets;

import java.io.IOException;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.project.example.model.Message;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String s) throws DecodeException {
        Message message;
        try {
            message = new ObjectMapper().readValue(s, Message.class);
        } catch (IOException e) {
            throw new DecodeException(s,e.getMessage(),e.getCause());
        }
        return message;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
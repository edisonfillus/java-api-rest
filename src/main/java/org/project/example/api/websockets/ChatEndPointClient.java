package org.project.example.api.websockets;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.project.example.model.Message;

@ClientEndpoint(decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndPointClient {

    private static Logger log = LogManager.getLogger(ChatEndPointClient.class);

    Session userSession = null;
    private MessageHandler messageHandler;

    public ChatEndPointClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.userSession = container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        log.info("websocket session opened on client");
        //this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason      the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        log.info("closing websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client
     * send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(Message message) {
        log.info("Message received on client: " + message.getContent());
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     * @throws EncodeException
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException, EncodeException {
        log.info("sending message to server: " + message.getContent());
        this.userSession.getBasicRemote().sendObject(message);
    }

    /**
     * Message handler.
     *
     */
    public static interface MessageHandler {
        public void handleMessage(Message message);
    }
}

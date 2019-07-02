package org.project.example.websockets;

import java.net.URI;

import javax.websocket.Session;
import javax.websocket.server.ServerContainer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class ChatEndPointServerTest {

    private static Server server;
    private static URI serverUri;
    private static Logger log = LogManager.getLogger(ChatEndPointServerTest.class);

    @BeforeAll
    public static void startJetty() throws Exception {
        // Create Server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        // Add Container and WebSocket Endpoint
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
        wscontainer.addEndpoint(ChatEndPointServer.class);
        server.setHandler(context);

        // Start Server
        server.start();

        // Determine Base URI for Server
        String host = connector.getHost() == null ? "localhost" : connector.getHost();
        int port = connector.getLocalPort();
        serverUri = new URI(String.format("ws://%s:%d", host, port));
    }

    @AfterAll
    public static void stopJetty()
    {
        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }

    @Test
    public void testConnect() throws Exception {
        /*URI uri = server.getWebsocketUri(WebSocketServer.class);*/
        URI uri = new URI(serverUri.toString() + "/chat/user");
        ChatEndPointClient clientEndPoint = new ChatEndPointClient(uri);

        // add listener
        clientEndPoint.addMessageHandler(new ChatEndPointClient.MessageHandler() {
            public void handleMessage(String message) {
                System.out.println(message);
            }
        });

        // send message to websocket
        clientEndPoint.sendMessage("{'content':'Hello World!'}");

    }

}
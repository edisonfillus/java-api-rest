package org.project.example.websockets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
import org.project.example.dto.Message;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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
    public void testSendMessage() throws Exception {

        // ### GIVEN
        URI uriUser1 = new URI(serverUri.toString() + "/chat/user1");
        URI uriUser2 = new URI(serverUri.toString() + "/chat/user2");
        final List<Message> broadcastedMessagesUser1 = new CopyOnWriteArrayList<>();
        final List<Message> broadcastedMessagesUser2 = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatchMsg1 = new CountDownLatch(2);
        CountDownLatch countDownLatchMsg2 = new CountDownLatch(2);
        Message msg1 = new Message("Test Message User 1");
        Message msg2 = new Message("Test Message User 2");

        // ### WHEN
        ChatEndPointClient clientEndPointUser1 = new ChatEndPointClient(uriUser1);
        clientEndPointUser1.addMessageHandler(new ChatEndPointClient.MessageHandler() {
            public void handleMessage(Message message) {
                broadcastedMessagesUser1.add(message);
                if(message.getContent().equals(msg1.getContent())){
                    countDownLatchMsg1.countDown();
                }
                if(message.getContent().equals(msg2.getContent())){
                    countDownLatchMsg2.countDown();
                }
            }
        });

        ChatEndPointClient clientEndPointUser2 = new ChatEndPointClient(uriUser2);
        clientEndPointUser2.addMessageHandler(new ChatEndPointClient.MessageHandler() {
            public void handleMessage(Message message) {
                broadcastedMessagesUser2.add(message);
                if(message.getContent().equals(msg1.getContent())){
                    countDownLatchMsg1.countDown();
                }
                if(message.getContent().equals(msg2.getContent())){
                    countDownLatchMsg2.countDown();
                }
            }
        });

        clientEndPointUser1.sendMessage(msg1); // send message to websocket
        countDownLatchMsg1.await(3,TimeUnit.SECONDS); // Block until message 1 arrive

        clientEndPointUser2.sendMessage(msg2); // send message to websocket
        countDownLatchMsg2.await(3,TimeUnit.SECONDS); // Block until message 2 arrive
        
        clientEndPointUser1.userSession.close();
        clientEndPointUser2.userSession.close();

        // ### THEN
        assertThat(broadcastedMessagesUser1.size(),is(2));
        assertThat(broadcastedMessagesUser2.size(),is(2));
        assertThat(broadcastedMessagesUser1.get(1).getContent(), is(msg2.getContent()));
        assertThat(broadcastedMessagesUser2.get(0).getContent(), is(msg1.getContent()));

    }


}
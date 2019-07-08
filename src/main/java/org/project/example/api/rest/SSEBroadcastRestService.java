package org.project.example.api.rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Singleton
@Path("broadcast")
public class SSEBroadcastRestService {
    private Sse sse;
    private SseBroadcaster broadcaster;

    static Logger log = LogManager.getLogger(SSEBroadcastRestService.class);

    public SSEBroadcastRestService(@Context final Sse sse) {
        this.sse = sse;
        this.broadcaster = sse.newBroadcaster();
        startEventBroadcast();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String broadcastMessage(String message) {
        final OutboundSseEvent event = sse.newEventBuilder().name("message").mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, message).build();

        broadcaster.broadcast(event);

        return "Message '" + message + "' has been broadcast.";
    }
    //curl -i http://localhost:8080/broadcast

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listenToBroadcast(@Context SseEventSink eventSink) {
        this.broadcaster.register(eventSink);
    }
    // curl -i -d "msg" -H "Content-Type: text/plain"  -X POST http://localhost:8080/broadcast


    public void startEventBroadcast() {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                // ... code that waits 1 second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(e);
                }

                JsonObject msg = Json.createObjectBuilder()
                    .add("id", i)
                    .add("ts", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Calendar.getInstance().getTime()))
                    .build();

                final OutboundSseEvent event = sse.newEventBuilder().name("updates")
                        .data(String.class, msg.toString()).build();
                broadcaster.broadcast(event);
            }
        }).start();
    }

}
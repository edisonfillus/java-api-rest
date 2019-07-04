package org.project.example.rest;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Path("events")
public class SSEEventRestService {

    static Logger log = LogManager.getLogger(SSEBroadcastRestService.class);

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void getServerSentEvents(@Context SseEventSink eventSink, @Context Sse sse) {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
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

                final OutboundSseEvent event = sse.newEventBuilder()
                    .id(String.valueOf(i))
                    .name("updates")
                    .data(String.class, msg.toString()).build();
                eventSink.send(event);
                log.info("Message sent: " + msg.toString());
            }
            eventSink.close();
        }).start();
    } 
    //curl -i http://localhost:8080/events

}

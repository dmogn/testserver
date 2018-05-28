package de.kenkou.jobtest.server;

import de.kenkou.jobtest.server.model.Event;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Events TCP endpoint. Based on simple blocking TCP socket.
 * Transfer all received and parsed errors to clients..
 * 
 * @author Dmitry Ognyannikov
 */
public class EventsServer implements Runnable {
    
    private final int port;
    
    private final ClientsServer clients;
    
    public EventsServer(int port, ClientsServer clients) {
        this.port = port;
        this.clients = clients;
    }
    
    @Override
    public void run() {
        try (ServerSocket eventsSocket = new ServerSocket(port)) {
            System.out.println("Server started");
            while (true) {
                Socket connectionSocket = eventsSocket.accept();
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), StandardCharsets.UTF_8.name()));
                BufferedWriter output = 
                        new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream(), StandardCharsets.UTF_8.name()));
                
                while (true) {
                    String eventLine = input.readLine();
                    //System.out.println("Received event: " + eventLine);
                    
                    if (eventLine == null || "".equals(eventLine))
                        continue;
                
                    Event event = parseLine(eventLine);
                    clients.pushEvent(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    private static Event parseLine(String line) {
        String fields[] = line.split("\\|");
        
        Event event = new Event(); 
        event.setLine(line);
        event.setSequenceId(Integer.valueOf(fields[0]));
        
        String type = fields[1];
        if ("F".equals(type)) {
            event.setType(Event.TYPE.FOLLOW);
            event.setFromUserId(Integer.valueOf(fields[2]));
            event.setToUserId(Integer.valueOf(fields[3]));
        } else if ("U".equals(type)) {
            event.setType(Event.TYPE.UNFOLLOW);
            event.setFromUserId(Integer.valueOf(fields[2]));
            event.setToUserId(Integer.valueOf(fields[3]));
        } else if ("B".equals(type)) {
            event.setType(Event.TYPE.BROADCAST);
        } else if ("P".equals(type)) {
            event.setType(Event.TYPE.PRIVATE_MSG);
            event.setFromUserId(Integer.valueOf(fields[2]));
            event.setToUserId(Integer.valueOf(fields[3]));
        } else if ("S".equals(type)) {
            event.setType(Event.TYPE.STATUS_UPDATE);
            event.setFromUserId(Integer.valueOf(fields[2]));
        }
        
        return event;
    }
}
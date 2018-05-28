package de.kenkou.jobtest.server;

import de.kenkou.jobtest.server.model.Event;
import de.kenkou.jobtest.server.model.Follow;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Clients TCP endpoint. Based on Netty embedded server.
 * 
 * @author Dmitry Ognyannikov
 */
public class ClientsServer extends Thread {
    
    private final int port;
    
    private final Map<Integer, ClientConnection> clientsMap = new TreeMap<>();
    
    private final Map<Integer, Set<Follow>> folowsFromUserMap = new TreeMap<>();
    
    public ClientsServer(int port) {
        this.port = port;
    }
    
    @Override
    public void run() {
        try (ServerSocket clientsSocket = new ServerSocket(port)) {
            while (true) {
                Socket connectionSocket = clientsSocket.accept();
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), StandardCharsets.UTF_8.name()));
                BufferedWriter output = 
                        new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream(), StandardCharsets.UTF_8.name()));
                
                // read client User ID
                String usertIdLine = input.readLine();
                int usertId = Integer.valueOf(usertIdLine);
                System.out.println("Client connected, ID: " + usertId);
                
                // close existing client connection
                if (clientsMap.containsKey(usertId) && !clientsMap.get(usertId).isClosed())
                    clientsMap.get(usertId).close();
                
                // add initialized connection to pool
                clientsMap.put(usertId, new ClientConnection(usertId, connectionSocket, input, output));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void pushEvent(Event event) throws IOException {
        if (event.getType() == Event.TYPE.FOLLOW) {
            // Follow: Only the To User Id should be notified
            addFollow(event.getFromUserId(), event.getToUserId());
            
            ClientConnection c = clientsMap.get(event.getToUserId());
            if (c != null)
                c.pushEvent(event);
            else
                System.out.println("Follow to user don't found: " + event.getToUserId());
        } else if (event.getType() == Event.TYPE.UNFOLLOW) {
            // Unfollow: No clients should be notified
            unFollow(event.getFromUserId(), event.getToUserId());
        } else if (event.getType() == Event.TYPE.BROADCAST) {
            // Broadcast: All connected user clients should be notified
            pushToAll(event);
        } else if (event.getType() == Event.TYPE.PRIVATE_MSG) {
            // Private Message: Only the To User Id should be notified
            ClientConnection c = clientsMap.get(event.getToUserId());
            if (c != null)
                c.pushEvent(event);
            else
                System.out.println("Private Message to user don't found: " + event.getToUserId());
        } else if (event.getType() == Event.TYPE.STATUS_UPDATE) {
            // Status Update: All current followers of the From User ID should be notified
            Set<Follow> folows = folowsFromUserMap.get(event.getFromUserId());
            if (folows != null)
                for (Follow f : folows) {
                    ClientConnection c = clientsMap.get(f.getToUserId());
                    if (c != null)
                        c.pushEvent(event);
                    else
                        System.out.println("Follower to user don't found: " + f.getToUserId());
                }
        }
    }
    
    private void pushToAll(Event event) throws IOException {
        for (ClientConnection c : clientsMap.values()) {
            if (!c.isClosed()) {
                c.pushEvent(event);
            }
        }
    }
    
    private void addFollow(int fromUserId, int toUserId) {
        Set<Follow> folows = folowsFromUserMap.get(fromUserId);
        
        if (folows == null) {
            // init folows for user
            folows = new HashSet<>();
            folowsFromUserMap.put(fromUserId, folows);
        }
        
        folows.add(new Follow(fromUserId, toUserId));
        
        System.out.println("Follow added. fromUserId: " + fromUserId + ", toUserId: " + toUserId);
    }
    
    private void unFollow(int fromUserId, int toUserId) {
        Set<Follow> folows = folowsFromUserMap.get(fromUserId);
            if (folows != null) {
                folows.remove(new Follow(fromUserId, toUserId));
            }
        
        
        System.out.println("Follow removed. fromUserId: " + fromUserId + ", toUserId: " + toUserId);
    }
}

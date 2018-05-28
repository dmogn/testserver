package de.kenkou.jobtest.server;

/**
 * Server initialization.
 * 
 * @author Dmitry Ognyannikov
 */
public class Server {
    
    public static void main(String args[]) {
        ClientsServer clients = new ClientsServer(9099);
        clients.start();
        
        EventsServer events = new EventsServer(9090, clients);
        
        // run cycle in main thread
        events.run();
    }
    
}

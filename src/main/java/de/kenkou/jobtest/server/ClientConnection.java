package de.kenkou.jobtest.server;

import de.kenkou.jobtest.server.model.Event;
import de.kenkou.jobtest.server.util.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import lombok.Data;

/**
 * Client blocking connection;
 * 
 * @author Dmitry Ognyannikov
 */
@Data
public class ClientConnection implements Comparable, Closeable {
    
    private final Socket connectionSocket;
    private final int userId;
    private final BufferedReader input;
    private final BufferedWriter output;
    
    public ClientConnection(int userId, Socket connectionSocket, BufferedReader input, BufferedWriter output) {
        this.userId = userId;
        this.connectionSocket = connectionSocket;
        this.input = input;
        this.output = output;        
    }
    
    public void pushEvent(Event event) throws IOException {
        if (isClosed()) {
            System.err.println("ERROR: Event to closed client connection. User ID: " + userId);
        }
        output.write(event.getLine());
        output.write(Utils.CRLF);
        output.flush();
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        connectionSocket.close();
    }
    
    public boolean isClosed() {
        return !connectionSocket.isConnected() || connectionSocket.isClosed();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ClientConnection) {
            ClientConnection cc = (ClientConnection) o;
            if(this.userId < cc.userId)
                return -1;

            if(this.userId > cc.userId)
                return 1;

            return 0;
        }
        else
            throw new IllegalArgumentException("ClientConnection is comparable with same type object only");
    }
}

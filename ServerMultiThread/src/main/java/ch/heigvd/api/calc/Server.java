package ch.heigvd.api.calc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {

        ServerSocket socket = null;

        try{
            socket = new ServerSocket(8167);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
            return;
        }

        Socket clientSocket = null;
        try {
            while (true){
                clientSocket = socket.accept();
                ServerWorker worker = new ServerWorker(clientSocket);
                new Thread(worker).start();
            }
        }
        catch (IOException ex){
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }

    }
}

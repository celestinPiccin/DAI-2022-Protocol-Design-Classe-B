package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
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
        Socket clientSocket  = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8876);
            clientSocket = serverSocket.accept();
            handleClient(clientSocket);
            serverSocket.close();
        }
        catch (IOException ex){
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {
        BufferedReader in = null;
        PrintWriter out = null;

        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
            out.println("HELLO");
            out.println("LIST OF COMMANDS:");
            out.println("- ADD 2");
            out.println("- SUB 2");
            out.println("END");
            out.flush();

            while(true) {
                out.println("ENTER YOUR COMMAND");
                out.flush();
                String line = in.readLine();
                if(line.equals("exit"))
                    break;

                String[] splitted = line.split(" ");
                if(splitted.length != 3) {
                    out.println("ERROR WITH ARGS");
                    out.flush();
                    continue;
                }

                String action = splitted[0];
                double val1, val2, res;

                try{
                    val1 = Double.parseDouble(splitted[1]);
                    val2 = Double.parseDouble(splitted[2]);
                }
                catch (NumberFormatException e){
                    out.println("NOT A DOUBLE");
                    out.flush();
                    continue;
                }

                switch (action){
                    case "ADD":
                        res = val1 + val2;
                        out.println(res);
                        out.flush();
                        break;
                    case "SUB":
                        res = val1 - val2;
                        out.println(res);
                        out.flush();
                        break;
                    default:
                        out.println("UNKNOWN COMMAND");
                        out.flush();
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            if(out != null) out.close();
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Socket clientSocket = null;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        try {
            this.clientSocket = clientSocket;
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        }catch (IOException ex){
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }

    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        try{
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
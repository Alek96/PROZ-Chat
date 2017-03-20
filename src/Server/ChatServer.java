package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/*
 1. The protocol should be enhanced so that the client can
    send clean disconnect messages to the server.
 
 2. The server should do some logging.
 */

/**
 * A multithreaded chat room server.  
 * When a client connects the server requests a screen name by sending 
 * the client the text "SUBMITNAME", and keeps requesting a name until
 * a unique one is received.  After a client submits a unique
 * name, the server acknowledges with "NAMEACCEPTED".  Then
 * all messages from that client will be broadcast to all other
 * clients that have submitted a unique screen name.  The
 * broadcast messages are prefixed with "MESSAGE ".
 * 
 * @author Aleksander 
 * @version 1.0
 */
public class ChatServer {

    /**
     * The port that the server listens on.
     */
    private static final int PORT = 9001;

    /**
     * The set of all names of clients in the chat room.
     */
    private final static HashSet<String> NAMES = new HashSet<String>();

    /**
     * The set of all the print writes for all the clients.
     */
    private final static HashSet<PrintWriter> WRITES = new HashSet<PrintWriter>();
    
    /**
     * The appplication main method, which listens on a port and
     * spawns handler threads.
     * 
     * @param args right now it is not used
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Could not listen on port " + PORT);
            System.exit(1);
        }
        
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class.  
     * Handlers are spawned from the listening loop and 
     * are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        private String name;
        private Socket socket = null;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Constructs a handler thread, squirreling away the socket.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Request a name from this client.  
         * Keep requesting until a name is submitted that is not already used.
         * 
         * @return if registerTheUserName was succeed
         * @throws IOException 
         */
        private boolean registerTheUserName() throws IOException {
            while (true) {
                out.println("SUBMITNAME");
                name = in.readLine();
                if (name == null) {
                    return false;
                }
                else if (!name.isEmpty() && !name.startsWith(" ")) {
                    synchronized (NAMES) {
                        if (!NAMES.contains(name)) {
                            NAMES.add(name);
                            break;
                        }
                    }
                }
            }
            return true;
        }
        
        /**
         * Accept messages from this client and broadcast them. 
         * Ignore other clients that cannot be broadcasted to.
         * 
         * @throws IOException 
         */
        private void broadcast() throws IOException {
            while (true) {
                String input = in.readLine();
                if (input == null) {
                    return;
                }
                if( !input.isEmpty() ) {
                    for (PrintWriter writer : WRITES) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            }
        }
        
        /**
         * Communication with user and broadcast message to others.
         */
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                if( !registerTheUserName() )
                    return;

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writes so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                WRITES.add(out);

                
                broadcast();
                
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is logged out.  
                // Remove its name and its print.
                // writer from the sets, and close its socket.
                if (name != null) {
                    NAMES.remove(name);
                }
                if (out != null) {
                    WRITES.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
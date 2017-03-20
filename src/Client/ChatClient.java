package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/** 
 * It's main class for connetion with gui and server.
 * After running user must write correct server adress, 
 * and next pick unique name, after that user can communicate with other users
 * 
 * @author Aleksander
 * @version 1.0
 */
public class ChatClient {

    /**
     * Input character streams for the socket.
     */
    BufferedReader in;
    /**
     * Output character streams for the socket.
     */
    PrintWriter out;
    /**
     * Special neme for user.
     * It is pick after connection the server.
     */
    String name;
    
    /**
     * gui application - this class make all visible part
     */
    GUI gui;

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.
     */
    public ChatClient() {
        gui = new GUI();
        gui.ActiveFrame();
    }
    

    /**
     * Connects to the server.
     * If adress of server will be unkonow, or network will be unreachable
     * then communique will be sand to the client
     */
    private Socket connectsToTheServer() throws IOException {
        Socket socket = null;
        String serverAddress;
        String mesageToClient[] = new String[3];
            mesageToClient[0]= "<html><font color='red'>";
            mesageToClient[1]= " ";
            mesageToClient[2]= "</font></html>";
        
        //it will be working until the connection will be made
        while( true ) {
            serverAddress = gui.getServerAddress(mesageToClient[0] + mesageToClient[1] + mesageToClient[2]);
            if( serverAddress == null ) 
                System.exit(1);
            else if( !serverAddress.isEmpty() ){
                try {
                    // Make connection and initialize streams
                    socket = new Socket(serverAddress, 9001);
                    break;
                } catch (java.net.UnknownHostException e) {
                    mesageToClient[1] = "Unknown host: " + serverAddress;
                    System.out.println(mesageToClient[1]);
                    
                } catch (java.net.SocketException e) {
                    mesageToClient[1] = "Network is unreachable: " + serverAddress;
                    System.out.println(mesageToClient[1]);
                } 
            }
            else
                mesageToClient[1]= "Expected hostname";
        }
        
        return socket;
    }
    
    
    /**
     * Initialize user name.
     * Proposed user name will be sent to the server in order to confirm.
     * After confirm textField will be actived
     */
    private void initializeUserName() throws IOException {
        System.out.println("initialize User Name");
        
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                name = gui.getName();
                //System.out.println(name);
                if( name == null )
                    System.exit(1);
                out.println(name);
            } else if (line.startsWith("NAMEACCEPTED")) {
                gui.setEditableTextField(true);
                break;
            }
        }
    }
    
    /**
     * Process messages from server.
     * Take message from server and shows on messageArea
     */
    private void ProcessMessagesFromServer() throws IOException {
        while (true) {
            String line = in.readLine();
            if (line.startsWith("MESSAGE")) {
                //Erase first word "MESSAGE".
                line = line.substring(8);
                
                //Check who send the message.
                int i=0;
                while( ! line.substring(i).startsWith(":") )
                    i++;
                
                if( line.substring(0,i).equals(name) ) {
                    gui.addNewMessage(name + ":\n");
                }
                else {
                    gui.addNewMessage(line.substring(0,i) + ":\n");
                }
                line = line.substring(i+2);
                gui.addNewMessage(line + "\n\n");
            }
        }
    }
    
    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        //Connect to the server
        Socket socket = connectsToTheServer();
        
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        gui.ActiveTextField(out);
        
        initializeUserName();
        ProcessMessagesFromServer();
    }

    /**
     * Runs the client as an application with a closeable frame.
     * @param args - it do nothing
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        //client.frame.dispose(); // frame do konstruktor
        client.run();
    }
}

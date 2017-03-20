package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

/**
 * It's main class for GUI application for Client.
 * 
 * @author Aleksander
 * @version 1.0
 */
public class GUI {

    /**
     * Main aplikacion window.
     */
    private final JFrame frame;// = new JFrame("Chatter");
    /**
     * Area where user can write message to other users.
     */
    private final JTextField textField = new JTextField(40);
    /**
     * Area for incoming messages.
     */
    private final JTextArea messageArea = new JTextArea(20, 40);
    /**
     * Used for autoscronn messageArea.
     */
    private final DefaultCaret caret = (DefaultCaret)messageArea.getCaret();

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.
     */
    public GUI() {
        frame = new JFrame("Chatter");
        
        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();
    }
    
    /**
     * Create new Listener in textField and connect him with out
     * 
     * @param out where do we want sent text from textField 
     */
    public void ActiveTextField(PrintWriter out) {
        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }
    
    /**
     *  Active Frame. that user can see application
     */
    public void ActiveFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Prompt for and return the address of the server.
     * It receives special message to client, which will be add to the Prompt.
     * 
     * @param mesageToClient. Message to client
     * @return return the address of the server.
     */
    public String getServerAddress(String mesageToClient) {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server: \n" + 
            mesageToClient,
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Prompt for and return the desired screen name.
     * @return Return chosen name
     */
    public String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    
    /**
     * Adding new message to messageArea
     * @param message - new message
     */
    public void addNewMessage(String message) {
        messageArea.append(message);
    }
    
    /**
     * set if textField is editable or not
     * @param value - true or false
     */
    public void setEditableTextField (boolean value) {
        textField.setEditable(value);
    }
            
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class CollaborativeClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static PrintWriter out;

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Collaborative Notepad");
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup connection to the server
        Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send updates to the server on typing
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                out.println(textArea.getText());
            }
        });

        // Listen for updates from the server
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    textArea.setText(message); // Update text area with server data
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        frame.pack();
        frame.setVisible(true);
    }
}

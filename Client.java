
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client {

    private JFrame frame;
    private JTextPane messageArea;
    private JTextField messageField;
    private JButton sendButton;

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private String clientName;
    private StyledDocument messageDoc;

    // Modern UI Colors
    private static final Color BACKGROUND_COLOR = new Color(33, 37, 41);
    private static final Color CARD_COLOR = new Color(52, 58, 64);
    private static final Color TEXT_COLOR = new Color(248, 249, 250);
    private static final Color ACCENT_COLOR = new Color(13, 110, 253);
    private static final Color ADMIN_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(25, 135, 84);

    private static final Font MODERN_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    private static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 18);

    public Client() {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ask for Server IP
        String serverIp = showModernInputDialog(
                "Enter Server IP Address:",
                "127.0.0.1"
        );

        if (serverIp == null || serverIp.trim().isEmpty()) {
            System.exit(0);
        }

        // Ask for Client Name
        clientName = showModernInputDialog(
                "Enter your name:",
                "Guest"
        );

        if (clientName == null || clientName.trim().isEmpty()) {
            clientName = "Guest" + (int) (Math.random() * 1000);
        }

        // Create Frame
        frame = new JFrame("Chat Client - " + clientName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Message Area
        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setFont(MODERN_FONT);
        messageArea.setBackground(CARD_COLOR);
        messageArea.setForeground(TEXT_COLOR);
        messageArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageDoc = messageArea.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(messageArea);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(108, 117, 125), 1
                )
        );

        scrollPane.getViewport().setBackground(CARD_COLOR);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);

        // Send button action
        sendButton.addActionListener(e -> sendMessage());

        // Press Enter to send
        messageField.addActionListener(e -> sendMessage());

        // Window closing
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                if (out != null) {
                    out.println(clientName + " is disconnecting.");
                }

                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setVisible(true);

        // Connect to server
        try {

            socket = new Socket(serverIp, 9999);

            out = new PrintWriter(
                    socket.getOutputStream(), true
            );

            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );

            // Send client name
            out.println(clientName);

            // Start server listener thread
            new Thread(new ServerListener()).start();

            // Welcome message
            appendStyledMessage(
                    "Welcome to the chat, " + clientName + "!",
                    SUCCESS_COLOR,
                    true
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Could not connect to the server:\n"
                            + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );

            System.exit(1);
        }
    }

    // Input dialog
    private String showModernInputDialog(
            String message,
            String defaultValue) {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(CARD_COLOR);
        panel.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        JLabel label = new JLabel(message);
        label.setForeground(TEXT_COLOR);
        label.setFont(MODERN_FONT);

        JTextField textField =
                new JTextField(defaultValue, 20);

        textField.setFont(MODERN_FONT);
        textField.setBackground(BACKGROUND_COLOR);
        textField.setForeground(TEXT_COLOR);

        textField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(108, 117, 125), 1
                        ),
                        new EmptyBorder(8, 8, 8, 8)
                )
        );

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Chat Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        }

        return null;
    }

    // Header panel
    private JPanel createHeaderPanel() {

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(BACKGROUND_COLOR);

        headerPanel.setBorder(
                new EmptyBorder(0, 0, 15, 0)
        );

        JLabel titleLabel =
                new JLabel("Chat Room", SwingConstants.CENTER);

        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel statusLabel =
                new JLabel(
                        "Connected as " + clientName,
                        SwingConstants.CENTER
                );

        statusLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.ITALIC,
                        12
                )
        );

        statusLabel.setForeground(SUCCESS_COLOR);

        headerPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        headerPanel.add(
                statusLabel,
                BorderLayout.CENTER
        );

        return headerPanel;
    }

    // Bottom panel
    private JPanel createBottomPanel() {

        JPanel bottomPanel =
                new JPanel(new BorderLayout(10, 0));

        bottomPanel.setBackground(BACKGROUND_COLOR);

        bottomPanel.setBorder(
                new EmptyBorder(15, 0, 0, 0)
        );

        messageField = new JTextField();

        messageField.setFont(MODERN_FONT);
        messageField.setBackground(CARD_COLOR);
        messageField.setForeground(TEXT_COLOR);

        messageField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(108, 117, 125), 1
                        ),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        messageField.setCaretColor(TEXT_COLOR);

        sendButton = new JButton("Send");

        sendButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        sendButton.setBackground(ACCENT_COLOR);
        sendButton.setForeground(Color.WHITE);

        sendButton.setBorder(
                new EmptyBorder(10, 20, 10, 20)
        );

        sendButton.setFocusPainted(false);

        sendButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        // Hover effect
        sendButton.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        sendButton.setBackground(
                                ACCENT_COLOR.brighter()
                        );
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        sendButton.setBackground(
                                ACCENT_COLOR
                        );
                    }
                }
        );

        bottomPanel.add(
                messageField,
                BorderLayout.CENTER
        );

        bottomPanel.add(
                sendButton,
                BorderLayout.EAST
        );

        return bottomPanel;
    }

    // Send message
    private void sendMessage() {

        String message =
                messageField.getText().trim();

        if (!message.isEmpty() && out != null) {

            out.println(
                    clientName + ": " + message
            );

            messageField.setText("");
        }
    }

    // Display message
    private void appendStyledMessage(
            String message,
            Color color,
            boolean bold) {

        SimpleAttributeSet attributes =
                new SimpleAttributeSet();

        StyleConstants.setForeground(
                attributes,
                color
        );

        StyleConstants.setFontFamily(
                attributes,
                "Segoe UI"
        );

        StyleConstants.setFontSize(
                attributes,
                14
        );

        StyleConstants.setBold(
                attributes,
                bold
        );

        try {

            messageDoc.insertString(
                    messageDoc.getLength(),
                    message + "\n",
                    attributes
            );

            messageArea.setCaretPosition(
                    messageDoc.getLength()
            );

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Listen for server messages
    class ServerListener implements Runnable {

        @Override
        public void run() {

            try {

                String serverMessage;

                while ((serverMessage = in.readLine()) != null) {

                    final String msg = serverMessage;

                    SwingUtilities.invokeLater(() -> {

                        if (msg.startsWith("[ADMIN]")) {

                            String adminMsg =
                                    msg.substring(7);

                            appendStyledMessage(
                                    "ADMIN: " + adminMsg,
                                    ADMIN_COLOR,
                                    true
                            );

                        } else {

                            appendStyledMessage(
                                    msg,
                                    TEXT_COLOR,
                                    false
                            );
                        }
                    });
                }

            } catch (IOException e) {

                SwingUtilities.invokeLater(() ->
                        appendStyledMessage(
                                "Connection to server lost.",
                                ADMIN_COLOR,
                                true
                        )
                );
            }
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                Client::new
        );
    }
}


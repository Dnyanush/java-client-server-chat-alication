import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField adminMessageField;
    private JButton sendAdminButton;

    private ServerSocket serverSocket;

    private final List<ClientHandler> clients =
            new ArrayList<>();

    // --------------------------------------------------
    // COLORS
    // --------------------------------------------------

    private static final Color ADMIN_BACKGROUND =
            new Color(25, 25, 28);

    private static final Color ADMIN_CARD =
            new Color(40, 40, 43);

    private static final Color ADMIN_TEXT =
            Color.WHITE;

    private static final Color ADMIN_ACCENT =
            new Color(255, 87, 51);

    private static final Color ADMIN_SUCCESS =
            new Color(76, 175, 80);

    private static final Color ADMIN_WARNING =
            new Color(255, 193, 7);

    private static final Font ADMIN_FONT =
            new Font("Monospaced", Font.PLAIN, 13);

    private static final Font ADMIN_TITLE_FONT =
            new Font("Monospaced", Font.BOLD, 16);

    // Server port
    private static final int PORT = 9999;

    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------

    public Server() {

        createServerWindow();

        log("Server started.");
        log("Waiting for clients on port " + PORT + "...");

        // Start server on separate thread
        new Thread(() -> startServer()).start();
    }

    // --------------------------------------------------
    // CREATE SERVER WINDOW
    // --------------------------------------------------

    private void createServerWindow() {

        frame = new JFrame("Chat Server - Admin Console");

        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.getContentPane().setBackground(
                ADMIN_BACKGROUND
        );

        // Main panel
        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                ADMIN_BACKGROUND
        );

        mainPanel.setBorder(
                new EmptyBorder(15, 15, 15, 15)
        );

        // Header
        JPanel headerPanel =
                createAdminHeaderPanel();

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // --------------------------------------------------
        // SERVER LOG AREA
        // --------------------------------------------------

        messageArea = new JTextArea();

        messageArea.setEditable(false);
        messageArea.setFont(ADMIN_FONT);
        messageArea.setBackground(ADMIN_CARD);
        messageArea.setForeground(ADMIN_TEXT);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        messageArea.setBorder(
                new EmptyBorder(10, 10, 10, 10)
        );

        JScrollPane scrollPane =
                new JScrollPane(messageArea);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                ADMIN_ACCENT,
                                2
                        ),
                        " Server Log ",
                        0,
                        0,
                        ADMIN_FONT,
                        ADMIN_TEXT
                )
        );

        scrollPane.getViewport().setBackground(
                ADMIN_CARD
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // --------------------------------------------------
        // ADMIN CONTROL PANEL
        // --------------------------------------------------

        JPanel controlPanel =
                createAdminControlPanel();

        mainPanel.add(
                controlPanel,
                BorderLayout.SOUTH
        );

        frame.add(mainPanel);

        frame.setVisible(true);
    }

    // --------------------------------------------------
    // START SERVER
    // --------------------------------------------------

    private void startServer() {

        try {

            serverSocket =
                    new ServerSocket(PORT);

            while (!serverSocket.isClosed()) {

                Socket clientSocket =
                        serverSocket.accept();

                log(
                        "Client connected: "
                                + clientSocket
                                .getInetAddress()
                                .getHostAddress()
                );

                ClientHandler clientHandler =
                        new ClientHandler(clientSocket);

                synchronized (clients) {

                    clients.add(clientHandler);
                }

                Thread clientThread =
                        new Thread(clientHandler);

                clientThread.start();
            }

        } catch (IOException e) {

            if (serverSocket != null
                    && !serverSocket.isClosed()) {

                log(
                        "Server error: "
                                + e.getMessage()
                );
            }
        }
    }

    // --------------------------------------------------
    // HEADER PANEL
    // --------------------------------------------------

    private JPanel createAdminHeaderPanel() {

        JPanel headerPanel =
                new JPanel(new BorderLayout());

        headerPanel.setBackground(
                ADMIN_BACKGROUND
        );

        headerPanel.setBorder(
                new EmptyBorder(0, 0, 20, 0)
        );

        JLabel titleLabel =
                new JLabel(
                        "ADMIN CONTROL CENTER",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                ADMIN_TITLE_FONT
        );

        titleLabel.setForeground(
                ADMIN_ACCENT
        );

        JLabel statusLabel =
                new JLabel(
                        "Server Online - Managing Chat Room",
                        SwingConstants.CENTER
                );

        statusLabel.setFont(
                new Font(
                        "Monospaced",
                        Font.ITALIC,
                        12
                )
        );

        statusLabel.setForeground(
                ADMIN_SUCCESS
        );

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

    // --------------------------------------------------
    // ADMIN CONTROL PANEL
    // --------------------------------------------------

    private JPanel createAdminControlPanel() {

        JPanel controlPanel =
                new JPanel(new BorderLayout());

        controlPanel.setBackground(
                ADMIN_BACKGROUND
        );

        controlPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                ADMIN_WARNING,
                                2
                        ),
                        " Admin Broadcast ",
                        0,
                        0,
                        ADMIN_FONT,
                        ADMIN_TEXT
                )
        );

        JPanel inputPanel =
                new JPanel(
                        new BorderLayout(10, 0)
                );

        inputPanel.setBackground(
                ADMIN_BACKGROUND
        );

        // Message field
        adminMessageField =
                new JTextField();

        adminMessageField.setFont(
                ADMIN_FONT
        );

        adminMessageField.setBackground(
                ADMIN_CARD
        );

        adminMessageField.setForeground(
                ADMIN_TEXT
        );

        adminMessageField.setCaretColor(
                ADMIN_TEXT
        );

        adminMessageField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.GRAY,
                                1
                        ),
                        new EmptyBorder(
                                8, 8, 8, 8
                        )
                )
        );

        // Broadcast button
        sendAdminButton =
                new JButton("BROADCAST");

        sendAdminButton.setFont(
                new Font(
                        "Monospaced",
                        Font.BOLD,
                        12
                )
        );

        sendAdminButton.setBackground(
                ADMIN_ACCENT
        );

        sendAdminButton.setForeground(
                Color.WHITE
        );

        sendAdminButton.setFocusPainted(false);

        sendAdminButton.setBorder(
                new EmptyBorder(
                        10, 20, 10, 20
                )
        );

        // Button click
        sendAdminButton.addActionListener(
                e -> sendAdminMessage()
        );

        // Enter key
        adminMessageField.addActionListener(
                e -> sendAdminMessage()
        );

        inputPanel.add(
                adminMessageField,
                BorderLayout.CENTER
        );

        inputPanel.add(
                sendAdminButton,
                BorderLayout.EAST
        );

        controlPanel.add(
                inputPanel,
                BorderLayout.CENTER
        );

        controlPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        controlPanel.getBorder(),
                        new EmptyBorder(
                                10, 10, 10, 10
                        )
                )
        );

        return controlPanel;
    }

    // --------------------------------------------------
    // ADMIN BROADCAST
    // --------------------------------------------------

    private void sendAdminMessage() {

        String message =
                adminMessageField
                        .getText()
                        .trim();

        if (message.isEmpty()) {
            return;
        }

        String adminMessage =
                "[ADMIN] " + message;

        log(
                "Broadcasting admin message: "
                        + message
        );

        synchronized (clients) {

            for (ClientHandler client : clients) {

                client.sendMessage(
                        adminMessage
                );
            }
        }

        adminMessageField.setText("");
    }

    // --------------------------------------------------
    // SERVER LOG
    // --------------------------------------------------

    private void log(String message) {

        String time =
                java.time.LocalTime
                        .now()
                        .toString();

        final String timestamp;

        if (time.length() > 8) {

            timestamp =
                    time.substring(0, 8);

        } else {

            timestamp = time;
        }

        // timestamp is now final
        SwingUtilities.invokeLater(() -> {

            if (messageArea != null) {

                messageArea.append(
                        "[" + timestamp + "] "
                                + message
                                + "\n"
                );

                messageArea.setCaretPosition(
                        messageArea.getDocument()
                                .getLength()
                );
            }
        });
    }

    // --------------------------------------------------
    // BROADCAST MESSAGE
    // --------------------------------------------------

    private void broadcastMessage(
            String message,
            ClientHandler sender) {

        log(
                "Broadcasting: " + message
        );

        synchronized (clients) {

            for (ClientHandler client : clients) {

                /*
                 * Send message to every client.
                 *
                 * This allows the sender to also
                 * see their own message.
                 */
                client.sendMessage(message);
            }
        }
    }

    // --------------------------------------------------
    // REMOVE CLIENT
    // --------------------------------------------------

    private void removeClient(
            ClientHandler client) {

        synchronized (clients) {

            clients.remove(client);
        }

        if (client.socket != null
                && !client.socket.isClosed()) {

            log(
                    "Client disconnected: "
                            + client.socket
                            .getInetAddress()
                            .getHostAddress()
            );
        }
    }

    // --------------------------------------------------
    // CLIENT HANDLER
    // --------------------------------------------------

    class ClientHandler implements Runnable {

        private final Socket socket;

        private PrintWriter out;
        private BufferedReader in;

        private String clientName;

        public ClientHandler(Socket socket) {

            this.socket = socket;
        }

        @Override
        public void run() {

            try {

                // Create output stream
                out = new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

                // Create input stream
                in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()
                        )
                );

                // First message is client name
                clientName =
                        in.readLine();

                if (clientName == null
                        || clientName.trim().isEmpty()) {

                    clientName =
                            "Anonymous@"
                                    + socket
                                    .getInetAddress()
                                    .getHostAddress();
                }

                log(
                        clientName
                                + " has joined the chat."
                );

                // Inform all other clients
                broadcastToOthers(
                        clientName
                                + " has joined the chat.",
                        this
                );

                String message;

                // Receive messages
                while (
                        (message = in.readLine())
                                != null
                ) {

                    message =
                            message.trim();

                    if (message.isEmpty()) {
                        continue;
                    }

                    // Send message to all clients
                    broadcastMessage(
                            message,
                            this
                    );

                    log(
                            "Message from "
                                    + clientName
                                    + ": "
                                    + message
                    );
                }

            } catch (IOException e) {

                log(
                        "Connection error for "
                                + clientName
                );

            } finally {

                // Remove client
                removeClient(this);

                if (clientName != null) {

                    broadcastMessage(
                            clientName
                                    + " has left the chat.",
                            null
                    );
                }

                try {

                    if (socket != null
                            && !socket.isClosed()) {

                        socket.close();
                    }

                } catch (IOException e) {

                    log(
                            "Error closing socket: "
                                    + e.getMessage()
                    );
                }
            }
        }

        // --------------------------------------------------
        // SEND MESSAGE
        // --------------------------------------------------

        public void sendMessage(
                String message) {

            if (out != null) {

                out.println(message);
                out.flush();
            }
        }
    }

    // --------------------------------------------------
    // SEND TO OTHER CLIENTS
    // --------------------------------------------------

    private void broadcastToOthers(
            String message,
            ClientHandler sender) {

        synchronized (clients) {

            for (ClientHandler client : clients) {

                if (client != sender) {

                    client.sendMessage(
                            message
                    );
                }
            }
        }
    }

    // --------------------------------------------------
    // MAIN METHOD
    // --------------------------------------------------

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> new Server()
        );
    }
}
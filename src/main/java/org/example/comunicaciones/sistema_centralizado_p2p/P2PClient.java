package org.example.comunicaciones.sistema_centralizado_p2p;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Clase que representa un nodo cliente P2P.
 * El cliente se registra en el servidor central y dispone de una interfaz para enviar y recibir mensajes.
 */
public class P2PClient extends JFrame {

    // Componentes de la interfaz gráfica
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    // Parámetros de conexión
    private final String serverIp;
    private final int serverPort;
    private final int clientListenPort;

    public P2PClient(String serverIp, int serverPort, int clientListenPort) {
        super("Cliente P2P - Escuchando en el puerto " + clientListenPort);
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientListenPort = clientListenPort;
        initComponents();
        // Registrar este cliente en el servidor central
        registerInServer();
        // Iniciar el hilo que escucha los mensajes reenviados por el servidor
        new Thread(new ClientListener(clientListenPort, chatArea)).start();
    }

    private void initComponents() {
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Enviar");
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    /**
     * Envía el mensaje ingresado al servidor central.
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // Se muestra el mensaje localmente
            chatArea.append("Yo: " + message + "\n");
            try (Socket socket = new Socket(serverIp, serverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("MESSAGE:" + message);
            } catch (IOException ex) {
                chatArea.append("Error enviando mensaje al servidor: " + ex.getMessage() + "\n");
            }
            inputField.setText("");
        }
    }

    /**
     * Registra el nodo cliente en el servidor central enviando el puerto de escucha.
     */
    private void registerInServer() {
        try (Socket socket = new Socket(serverIp, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("REGISTER:" + clientListenPort);
        } catch (IOException ex) {
            chatArea.append("Error registrando en el servidor: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        /*
         * Para ejecutar el cliente se deben proporcionar los siguientes parámetros:
         *  - IP del servidor central (ej. "127.0.0.1")
         *  - Puerto del servidor central (ej. 13000)
         *  - Puerto de escucha para este cliente (ej. 12000, 12001, 12002, etc.)
         */
        if (args.length < 3) {
            System.err.println("Uso: java P2PClient <serverIp> <serverPort> <clientListenPort>");
            System.exit(1);
        }
        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int clientListenPort = Integer.parseInt(args[2]);

        SwingUtilities.invokeLater(() -> {
            P2PClient client = new P2PClient(serverIp, serverPort, clientListenPort);
            client.setVisible(true);
        });
    }
}

/**
 * Hilo que escucha en el puerto designado para recibir mensajes reenviados por el servidor central.
 */
class ClientListener implements Runnable {
    private final int listenPort;
    private final JTextArea chatArea;

    public ClientListener(int listenPort, JTextArea chatArea) {
        this.listenPort = listenPort;
        this.chatArea = chatArea;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> processMessage(socket)).start();
            }
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> chatArea.append("Error en el listener: " + ex.getMessage() + "\n"));
        }
    }

    private void processMessage(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message = in.readLine();
            if (message != null) {
                SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
            }
        } catch (IOException ex) {
            SwingUtilities.invokeLater(() -> chatArea.append("Error recibiendo mensaje: " + ex.getMessage() + "\n"));
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignorar error al cerrar socket
            }
        }
    }
}

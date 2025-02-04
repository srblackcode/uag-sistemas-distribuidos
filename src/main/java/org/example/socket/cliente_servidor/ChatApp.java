package org.example.socket.cliente_servidor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 */

public class ChatApp extends JFrame {

    // Componentes de la interfaz gráfica
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    // Configuración de nodos: lista de IPs y puertos
    private List<String> targetIPs = new ArrayList<>();
    private List<Integer> targetPorts = new ArrayList<>();

    // Puerto de escucha para este nodo
    private int listenPort;

    public ChatApp(int listenPort, List<String> ips, List<Integer> ports) {
        super("Chat P2P Multihilo - Puerto " + listenPort);
        this.listenPort = listenPort;
        this.targetIPs = ips;
        this.targetPorts = ports;
        initComponents();
        // Lanzar el hilo del servidor para recibir mensajes
        new ChatServer(listenPort, chatArea).start();
    }

    private void initComponents() {
        // Configuración básica de la ventana
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Área para visualizar los mensajes (solo lectura)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para el ingreso y envío de mensajes
        JPanel panel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Enviar");
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        // Definir la acción del botón y la tecla Enter
        sendButton.addActionListener(e -> enviarMensaje());
        inputField.addActionListener(e -> enviarMensaje());
    }

    /**
     * Envía el mensaje ingresado a todos los nodos configurados, excepto a este mismo.
     */
    private void enviarMensaje() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("Yo: " + message + "\n");
            // Enviar el mensaje a cada nodo destino en un hilo independiente
            for (int i = 0; i < targetIPs.size(); i++) {
                if (targetPorts.get(i) == listenPort) {
                    continue;
                }
                int finalI = i;
                new Thread(() -> ChatClient.sendMessage(targetIPs.get(finalI), targetPorts.get(finalI), message)).start();
            }
            inputField.setText("");
        }
    }

    /**
     * Método principal de la aplicación.
     * Se debe ejecutar pasando el puerto de escucha como argumento (ej. 12000).
     */
    public static void main(String[] args) {
        int listenPort = 12000; // Valor predeterminado
        if (args.length > 0) {
            try {
                listenPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Puerto inválido. Se usará el valor predeterminado 12000.");
            }
        }

        // Configuración de nodos: utilizando 127.0.0.1 para pruebas en red local
        List<String> ips = new ArrayList<>();
        List<Integer> ports = new ArrayList<>();
        ips.add("127.0.0.1"); ports.add(12000);
        ips.add("127.0.0.1"); ports.add(12001);
        ips.add("127.0.0.1"); ports.add(12002);

        int finalListenPort = listenPort;
        SwingUtilities.invokeLater(() -> {
            ChatApp app = new ChatApp(finalListenPort, ips, ports);
            app.setVisible(true);
        });
    }
}

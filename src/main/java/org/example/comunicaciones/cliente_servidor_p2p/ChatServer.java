package org.example.comunicaciones.cliente_servidor_p2p;

import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 */

public class ChatServer extends Thread {
    private int port;
    private JTextArea chatArea;

    public ChatServer(int port, JTextArea chatArea) {
        this.port = port;
        this.chatArea = chatArea;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            appendChat("Servidor iniciado en el puerto " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException ex) {
            appendChat("Error en el servidor: " + ex.getMessage());
        }
    }

    /**
     * Procesa la conexión entrante y lee el mensaje del cliente.
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String receivedMessage = in.readLine();
            if (receivedMessage != null) {
                appendChat("Remoto: " + receivedMessage);
            }
        } catch (IOException ex) {
            appendChat("Error al leer mensaje: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Error al cerrar el socket, se ignora.
            }
        }
    }

    /**
     * Actualiza el área de chat de forma segura en el hilo de la GUI.
     */
    private void appendChat(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }
}


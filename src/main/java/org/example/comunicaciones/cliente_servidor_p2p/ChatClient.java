package org.example.comunicaciones.cliente_servidor_p2p;

import java.io.*;
import java.net.*;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 */

public class ChatClient {

    /**
     * Envía el mensaje al nodo especificado mediante su IP y puerto.
     */
    public static void sendMessage(String ip, int port, String message) {
        try (Socket socket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException ex) {
            System.err.println("Error enviando mensaje a " + ip + ":" + port + " - " + ex.getMessage());
        }
    }
}


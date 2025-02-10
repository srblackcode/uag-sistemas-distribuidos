package org.example.comunicaciones.juego_gato;

import java.io.*;
import java.net.*;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 */

class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public GameClient(String serverIp, int serverPort, TicTacToeGUI gui) {
        try {
            socket = new Socket(serverIp, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        gui.updateBoard(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(String move) {
        out.println(move);
    }
}
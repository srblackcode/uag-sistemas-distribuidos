package org.example.comunicaciones.juego_pacman;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.Point;
import java.util.*;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 * PacmanClient
 */

public class PacmanClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private PacmanGUI gui;
    private int playerId;

    // Clase auxiliar para diferenciar pellets
    public static class Coin {
        public Point point;
        public boolean isPower;
        public Coin(Point point, boolean isPower) {
            this.point = point;
            this.isPower = isPower;
        }
    }

    public PacmanClient(String serverIp, int serverPort, PacmanGUI gui) {
        this.gui = gui;
        try {
            socket = new Socket(serverIp, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("PLAYER:")) {
                            playerId = Integer.parseInt(message.substring(7));
                        } else if (message.startsWith("STATE:")) {
                            parseGameState(message.substring(6));
                        } else if (message.startsWith("WINNER:")) {
                            JOptionPane.showMessageDialog(gui, "¡Ganador: Jugador " + message.substring(7) + "!");
                        } else if (message.equals("GAMEOVER")) {
                            JOptionPane.showMessageDialog(gui, "¡Juego Terminado!");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendMove(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    private void parseGameState(String data) {
        String[] tokens = data.split(";");
        Map<Integer, Pacman> players = new HashMap<>();
        List<Coin> coins = new ArrayList<>();
        List<Point> walls = new ArrayList<>();
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            String[] parts = token.split(",");
            switch (parts[0]) {
                case "P": {
                    int id = Integer.parseInt(parts[1]);
                    PlayerRole role = PlayerRole.valueOf(parts[2]);
                    String ghostColor = parts[3];
                    int x = Integer.parseInt(parts[4]);
                    int y = Integer.parseInt(parts[5]);
                    boolean powered = parts[6].equals("1");
                    Pacman p = new Pacman(id);
                    p.role = role;
                    p.ghostColor = ghostColor.equals("NONE") ? null : ghostColor;
                    p.x = x;
                    p.y = y;
                    p.poweredUp = powered;
                    players.put(id, p);
                    break;
                }
                case "C": {
                    int cx = Integer.parseInt(parts[1]);
                    int cy = Integer.parseInt(parts[2]);
                    // Si se envía un cuarto parámetro, se interpreta: "1" power, "0" normal.
                    boolean isPower = (parts.length >= 4 && parts[3].equals("1"));
                    coins.add(new Coin(new Point(cx, cy), isPower));
                    break;
                }
                case "W": {
                    int wx = Integer.parseInt(parts[1]);
                    int wy = Integer.parseInt(parts[2]);
                    walls.add(new Point(wx, wy));
                    break;
                }
            }
        }
        gui.updateBoard(players, coins, walls);
    }
}





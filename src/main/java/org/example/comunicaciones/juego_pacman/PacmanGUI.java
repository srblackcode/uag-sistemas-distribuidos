package org.example.comunicaciones.juego_pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Point;
import java.util.List;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 * PacmanGUI
 */

public class PacmanGUI extends JFrame {
    private final int rows = 36;
    private final int cols = 28;
    private PacmanClient client;
    private JLabel[][] grid;
    private Map<String, ImageIcon> assets;

    public PacmanGUI() {
        setTitle("Pacman P2P Game");
        setSize(600, 600);
        setLayout(new GridLayout(rows, cols));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Cargar los assets
        loadAssets();

        grid = new JLabel[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setBackground(Color.BLACK);
                grid[i][j] = label;
                add(label);
            }
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                String command = null;
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_UP:    command = "MOVE:UP";    break;
                    case KeyEvent.VK_DOWN:  command = "MOVE:DOWN";  break;
                    case KeyEvent.VK_LEFT:  command = "MOVE:LEFT";  break;
                    case KeyEvent.VK_RIGHT: command = "MOVE:RIGHT"; break;
                }
                if (command != null && client != null) {
                    client.sendMove(command);
                }
            }
        });

        setFocusable(true);
        setVisible(true);
        requestFocusInWindow();
    }

    private void loadAssets() {
        assets = new HashMap<>();
        String[] assetNames = { "wall", "cherry", "cherry2", "orangeGhost", "pacmanDown", "pacmanLeft",
                "pacmanRight", "pacmanUp", "pinkGhost", "powerFood", "redGhost", "scaredGhost", "blueGhost" };

        for (String asset : assetNames) {
            assets.put(asset, new ImageIcon(getClass().getResource("/" + asset + ".png")));
        }
    }

    public void setClient(PacmanClient client) {
        this.client = client;
    }

    public void updateBoard(Map<Integer, Pacman> players, List<PacmanClient.Coin> coins, List<Point> walls) {
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                grid[i][j].setIcon(null);
                grid[i][j].setBackground(Color.BLACK);
            }
        }

        for (Point p : walls) {
            grid[p.x][p.y].setIcon(assets.get("wall"));
        }

        for (PacmanClient.Coin coin : coins) {
            grid[coin.point.x][coin.point.y].setIcon(coin.isPower ? assets.get("powerFood") : assets.get("cherry"));
        }

        for (Pacman pac : players.values()) {
            ImageIcon icon;
            if (pac.role == PlayerRole.PACMAN) {
                icon = getPacmanIcon(pac);
            } else {
                icon = pac.poweredUp ? assets.get("scaredGhost") : getGhostIcon(pac.ghostColor);
            }
            grid[pac.x][pac.y].setIcon(icon);
        }
    }

    private ImageIcon getPacmanIcon(Pacman pac) {
        switch (pac.role) {
            case PACMAN:
                return assets.get("pacmanRight");
            default:
                return null;
        }
    }

    private ImageIcon getGhostIcon(String ghostColor) {
        if (ghostColor == null) return assets.get("blueGhost");
        switch (ghostColor.toUpperCase()) {
            case "RED":    return assets.get("redGhost");
            case "PINK":   return assets.get("pinkGhost");
            case "BLUE":   return assets.get("blueGhost");
            case "ORANGE": return assets.get("orangeGhost");
            default:        return assets.get("blueGhost");
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java PacmanGUI <serverIp> <serverPort>");
            System.exit(1);
        }
        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);
        PacmanGUI gui = new PacmanGUI();
        PacmanClient client = new PacmanClient(serverIp, serverPort, gui);
        gui.setClient(client);
    }
}



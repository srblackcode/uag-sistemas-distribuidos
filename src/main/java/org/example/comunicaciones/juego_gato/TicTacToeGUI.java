package org.example.comunicaciones.juego_gato;

import javax.swing.*;
import java.awt.*;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Interfaz gráfica del juego
 */

class TicTacToeGUI extends JFrame {
    private final GameLogic gameLogic;
    private final JButton[][] board = new JButton[3][3];
    private GameClient client;
    private boolean isMyTurn = true;
    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setText(" ");
            }
        }
    }

    public TicTacToeGUI(GameClient client) {
        this.gameLogic = new GameLogic();
        this.client = client;
        setTitle("Juego del Gato P2P");
        setSize(300, 300);
        setLayout(new GridLayout(3, 3));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = new JButton(" ");
                final int x = i, y = j;
                board[i][j].addActionListener(e -> makeMove(x, y));
                add(board[i][j]);
            }
        }
        setVisible(true);
    }

    public void setClient(GameClient client) {
        this.client = client;
    }

    private void makeMove(int x, int y) {
        if (!isMyTurn || !board[x][y].getText().equals(" ") || !gameLogic.makeMove(x, y, "X")) return;
        board[x][y].setText("X");
        if (gameLogic.checkWin("X")) {
            JOptionPane.showMessageDialog(this, "¡Ganaste!");
            gameLogic.resetBoard();
            resetBoard();
            return;
        }
        if (gameLogic.isDraw()) {
            JOptionPane.showMessageDialog(this, "¡Empate!");
            gameLogic.resetBoard();
            resetBoard();
            return;
        }
        isMyTurn = false;
        client.sendMove("MOVE:" + x + "," + y);
        if (!isMyTurn || !board[x][y].getText().equals(" ")) return;
        board[x][y].setText("X");
        isMyTurn = false;
        client.sendMove("MOVE:" + x + "," + y);
    }

    public void updateBoard(String message) {
        if (message.startsWith("MOVE:")) {
            String[] parts = message.substring(5).split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (gameLogic.makeMove(x, y, "O")) {
                board[x][y].setText("O");
                if (gameLogic.checkWin("O")) {
                    JOptionPane.showMessageDialog(this, "¡Perdiste!");
                    gameLogic.resetBoard();
                    resetBoard();
                    return;
                }
                if (gameLogic.isDraw()) {
                    JOptionPane.showMessageDialog(this, "¡Empate!");
                    gameLogic.resetBoard();
                    resetBoard();
                    return;
                }
                isMyTurn = true;
            }
        }
        if (message.startsWith("MOVE:")) {
            String[] parts = message.substring(5).split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            board[x][y].setText("O");
            isMyTurn = true;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java TicTacToeGUI <serverIp> <serverPort>");
            System.exit(1);
        }
        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // Crear la GUI
        TicTacToeGUI gui = new TicTacToeGUI(null);

        // Inicializar el cliente y asociarlo a la GUI
        GameClient client = new GameClient(serverIp, serverPort, gui);

        gui.setClient(client);
    }
}
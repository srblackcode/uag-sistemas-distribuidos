package org.example.comunicaciones.juego_gato;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Lógica del juego
 */

class GameLogic {
    private String[][] board;

    public GameLogic() {
        board = new String[3][3];
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = " ";
            }
        }
    }

    public boolean makeMove(int x, int y, String player) {
        if (board[x][y].equals(" ")) {
            board[x][y] = player;
            return true;
        }
        return false;
    }

    public boolean checkWin(String player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player)) return true;
            if (board[0][i].equals(player) && board[1][i].equals(player) && board[2][i].equals(player)) return true;
        }
        if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) return true;
        if (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player)) return true;
        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(" ")) return false;
            }
        }
        return true;
    }
}

package org.example.comunicaciones.juego_pacman;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 * Representa a un jugador (sea Pac-Man o Fantasma).
 * Interpreta un mapa definido por una cadena.
 * Se usan 28 columnas x 36 filas (por ejemplo, para Ms. Pac‑Man 1).
 * Caracteres:
 *   '|' o '_'  : pared
 *   '*'        : pellet normal
 *   'o'        : pellet de poder
 *   Otros     : zona libre.
 */

public class MsPacmanMap {
    private int width;
    private int height;
    private char[][] layout;

    public String name;
    public String wallFillColor;
    public String wallStrokeColor;
    public String pelletColor;

    public MsPacmanMap(int width, int height, String layoutStr) {
        this.width = width;
        this.height = height;
        layout = new char[height][width];
        int index = 0;
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                layout[i][j] = layoutStr.charAt(index++);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char getCell(int row, int col) {
        return layout[row][col];
    }

    public boolean isWall(int row, int col) {
        char c = getCell(row, col);
        return (c == '|' || c == '_');
    }

    // Ahora, un pellet normal se indicará con '*' en el mapa.
    public boolean hasPellet(int row, int col) {
        return getCell(row, col) == '*';
    }

    public boolean hasPowerPellet(int row, int col) {
        return getCell(row, col) == 'o';
    }
}


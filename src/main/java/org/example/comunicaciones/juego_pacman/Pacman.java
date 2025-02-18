package org.example.comunicaciones.juego_pacman;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 * Representa a un jugador (sea Pac-Man o Fantasma).
 */

public class Pacman {
    public int id;
    public int x;
    public int y;

    // Rol del jugador
    public PlayerRole role;

    // Para fantasmas, se asigna un color (por ejemplo, "RED", "PINK", etc.)
    public String ghostColor;

    // Indica si tiene superpoder
    public boolean poweredUp = false;

    // Momento (en milisegundos) en que expira el poder
    public long powerEndTime = 0;

    public Pacman(int id) {
        this.id = id;
    }
}



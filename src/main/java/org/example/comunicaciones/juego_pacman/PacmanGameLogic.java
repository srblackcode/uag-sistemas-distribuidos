package org.example.comunicaciones.juego_pacman;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 * Lógica del juego a partir de un mapa (MsPacmanMap).
 */

public class PacmanGameLogic {
    private int rows;
    private int cols;
    private MsPacmanMap map;

    // Cada celda del tablero
    class Cell {
        boolean hasCoin = false;
        boolean isPowerPellet = false;
        boolean isWall = false;
        Set<Integer> pacmans = new HashSet<>();
    }

    // Matriz de celdas
    Cell[][] grid;

    // Jugadores (id -> Pacman)
    private Map<Integer, Pacman> players;

    // Diccionario de comandos (movimiento)
    private Map<String, BiConsumer<Pacman, PacmanGameLogic>> commands;

    private static final long POWER_DURATION_MS = 10000; // 5 segundos

    public PacmanGameLogic(MsPacmanMap map) {
        this.map = map;
        this.rows = map.getHeight();
        this.cols = map.getWidth();
        grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                grid[i][j] = new Cell();
                if (map.isWall(i, j)) {
                    grid[i][j].isWall = true;
                }
                // Si hay pellet normal
                if (map.hasPellet(i, j)) {
                    grid[i][j].hasCoin = true;
                    grid[i][j].isPowerPellet = false;
                }
                // Si hay pellet de poder
                if (map.hasPowerPellet(i, j)) {
                    grid[i][j].hasCoin = true;
                    grid[i][j].isPowerPellet = true;
                }
            }
        }
        players = new ConcurrentHashMap<>();
        commands = new HashMap<>();
        initCommands();
    }

    private void initCommands() {
        commands.put("MOVE:UP",    (p, logic) -> logic.movePacman(p, -1,  0));
        commands.put("MOVE:DOWN",  (p, logic) -> logic.movePacman(p,  1,  0));
        commands.put("MOVE:LEFT",  (p, logic) -> logic.movePacman(p,  0, -1));
        commands.put("MOVE:RIGHT", (p, logic) -> logic.movePacman(p,  0,  1));
    }

    public MsPacmanMap getMap() {
        return map;
    }

    public void addPlayer(Pacman p) {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(rows);
            y = rand.nextInt(cols);
        } while (grid[x][y].isWall);
        p.x = x;
        p.y = y;
        players.put(p.id, p);
        grid[x][y].pacmans.add(p.id);
    }

    public void removePlayer(int playerId) {
        Pacman p = players.get(playerId);
        if (p != null) {
            grid[p.x][p.y].pacmans.remove(p.id);
            players.remove(playerId);
        }
    }

    public Collection<Pacman> getPlayers() {
        return players.values();
    }

    public boolean hasCoin(int x, int y) {
        return grid[x][y].hasCoin;
    }

    public boolean isWall(int x, int y) {
        return grid[x][y].isWall;
    }

    // Cuando se consume un pellet, se genera uno nuevo aleatoriamente.
    private void spawnRandomCoin() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(rows);
            y = rand.nextInt(cols);
        } while (grid[x][y].isWall);
        // Con baja probabilidad (por ejemplo, 10%) generar un pellet de poder.
        if (rand.nextDouble() < 0.1) {
            grid[x][y].hasCoin = true;
            grid[x][y].isPowerPellet = true;
        } else {
            grid[x][y].hasCoin = true;
            grid[x][y].isPowerPellet = false;
        }
    }

    public void processCommand(int playerId, String command) {
        Pacman p = players.get(playerId);
        if (p == null || !commands.containsKey(command)) return;

        // Mover al jugador
        commands.get(command).accept(p, this);

        // Si hay pellet en la celda, el jugador lo recoge
        Cell cell = grid[p.x][p.y];
        if (cell.hasCoin) {
            // Eliminar superpoder de todos antes de asignarlo a p
            for (Pacman other : players.values()) {
                other.poweredUp = false;
            }
            p.poweredUp = true;
            p.powerEndTime = System.currentTimeMillis() + POWER_DURATION_MS;
            cell.hasCoin = false;
            spawnRandomCoin();
        }

        // Revisar colisiones en la celda
        checkCollisions(p);

        // Revocar el poder si expiró
        for (Pacman other : players.values()) {
            if (other.poweredUp && System.currentTimeMillis() > other.powerEndTime) {
                other.poweredUp = false;
            }
        }
    }

    private void movePacman(Pacman p, int dx, int dy) {
        int newX = (p.x + dx + rows) % rows;
        int newY = (p.y + dy + cols) % cols;
        if (grid[newX][newY].isWall) return;
        grid[p.x][p.y].pacmans.remove(p.id);
        p.x = newX;
        p.y = newY;
        grid[newX][newY].pacmans.add(p.id);
    }

    private void checkCollisions(Pacman p) {
        Cell cell = grid[p.x][p.y];
        if (cell.pacmans.size() <= 1) return;
        for (int otherId : new HashSet<>(cell.pacmans)) {
            if (otherId == p.id) continue;
            Pacman other = players.get(otherId);
            if (other == null) continue;
            resolveCollision(p, other);
        }
    }

    private void resolveCollision(Pacman a, Pacman b) {
        // Si uno tiene poder y el otro no, el que no lo tiene es eliminado.
        if (a.poweredUp && !b.poweredUp) {
            removePlayer(b.id);
            return;
        }
        if (b.poweredUp && !a.poweredUp) {
            removePlayer(a.id);
            return;
        }
        // En otros casos, no se realiza acción.
    }

    public boolean isGameOver() {
        return players.size() <= 1;
    }
}

package org.example.comunicaciones.juego_pacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Cliente del juego
 * PacmanServer
 */

public class PacmanServer {
    private static final int PORT = 13000;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static PacmanGameLogic gameLogic;
    private static int nextPlayerId = 1;

    public static void main(String[] args) {
        String layout =
                "____________________________" +
                        "____________________________" +
                        "____________________________" +
                        "||||||||||||||||||||||||||||" +
                        "|......||..........||......|" +
                        "|o||||.||.||||||||.||.||||o|" +
                        "|.||||.||.||||||||.||.||||.|" +
                        "|..........................|" +
                        "|||.||.|||||.||.|||||.||.|||" +
                        "__|.||.|||||.||.|||||.||.|__" +
                        "|||.||.|||||.||.|||||.||.|||" +
                        "   .||.......||.......||.   " +
                        "|||.||||| |||||||| |||||.|||" +
                        "__|.||||| |||||||| |||||.|__" +
                        "__|.                    .|__" +
                        "__|.||||| |||--||| |||||.|__" +
                        "__|.||||| |______| |||||.|__" +
                        "__|.||    |______|    ||.|__" +
                        "__|.|| || |______| || ||.|__" +
                        "|||.|| || |||||||| || ||.|||" +
                        "   .   ||          ||   .   " +
                        "|||.|||||||| || ||||||||.|||" +
                        "__|.|||||||| || ||||||||.|__" +
                        "__|.......   ||   .......|__" +
                        "__|.|||||.||||||||.|||||.|__" +
                        "|||.|||||.||||||||.|||||.|||" +
                        "|............  ............|" +
                        "|.||||.|||||.||.|||||.||||.|" +
                        "|.||||.|||||.||.|||||.||||.|" +
                        "|.||||.||....||....||.||||.|" +
                        "|o||||.||.||||||||.||.||||o|" +
                        "|.||||.||.||||||||.||.||||.|" +
                        "|..........................|" +
                        "||||||||||||||||||||||||||||" +
                        "____________________________" +
                        "____________________________";

        MsPacmanMap map = new MsPacmanMap(28, 36, layout);
        map.name = "Ms. Pac-Man 1";
        map.wallFillColor = "#FFB8AE";
        map.wallStrokeColor = "#FF0000";
        map.pelletColor = "#dedeff";

        gameLogic = new PacmanGameLogic(map);

        System.out.println("Servidor Pacman iniciado en el puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                int playerId = nextPlayerId++;

                // Asignar rol: el primer jugador es PACMAN, los demás son GHOST
                Pacman player = new Pacman(playerId);
                if (playerId == 1) {
                    player.role = PlayerRole.PACMAN;
                } else {
                    player.role = PlayerRole.GHOST;
                    switch (playerId) {
                        case 2:
                            player.ghostColor = "RED";
                            break;
                        case 3:
                            player.ghostColor = "PINK";
                            break;
                        case 4:
                            player.ghostColor = "BLUE";
                            break;
                        case 5:
                            player.ghostColor = "ORANGE";
                            break;
                        default:
                            player.ghostColor = "WHITE";
                            break;
                    }
                }
                gameLogic.addPlayer(player);

                ClientHandler client = new ClientHandler(socket, playerId);
                clients.add(client);
                new Thread(client).start();

                broadcastGameState();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcastGameState() {
        String state = serializeGameState();
        for (ClientHandler client : clients) {
            if (client.out != null) {
                client.sendMessage(state);
            }
        }
    }

    private static String serializeGameState() {
        StringBuilder sb = new StringBuilder("STATE:");
        // Serializar jugadores: "P,id,role,color,x,y,power;"
        for (Pacman p : gameLogic.getPlayers()) {
            sb.append("P,")
                    .append(p.id).append(",")
                    .append(p.role).append(",")
                    .append(p.ghostColor == null ? "NONE" : p.ghostColor).append(",")
                    .append(p.x).append(",")
                    .append(p.y).append(",")
                    .append(p.poweredUp ? "1" : "0").append(";");
        }
        // Serializar pellets (monedas)
        for (int i = 0; i < gameLogic.getMap().getHeight(); i++) {
            for (int j = 0; j < gameLogic.getMap().getWidth(); j++) {
                if (gameLogic.hasCoin(i, j)) {
                    sb.append("C,").append(i).append(",").append(j).append(";");
                }
            }
        }
        // Serializar paredes
        for (int i = 0; i < gameLogic.getMap().getHeight(); i++) {
            for (int j = 0; j < gameLogic.getMap().getWidth(); j++) {
                if (gameLogic.isWall(i, j)) {
                    sb.append("W,").append(i).append(",").append(j).append(";");
                }
            }
        }
        return sb.toString();
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        BufferedReader in;
        PrintWriter out;
        private int playerId;

        public ClientHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("PLAYER:" + playerId);
                String message;
                while ((message = in.readLine()) != null) {
                    gameLogic.processCommand(playerId, message);
                    broadcastGameState();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }
    }
}



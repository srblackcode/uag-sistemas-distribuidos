package org.example.comunicaciones.sistema_centralizado_p2p;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 * Clase que representa el servidor central.
 * Permite el registro de nodos y el reenvío de mensajes a todos los clientes registrados.
 */

public class CentralServer {

    // Lista de clientes registrados (cada elemento contiene la IP y el puerto de escucha)
    private static final List<ClientInfo> clients = new ArrayList<>();

    // Puerto en el que el servidor central escuchará
    private static final int SERVER_PORT = 13000;

    public static void main(String[] args) {
        System.out.println("Servidor central iniciado en el puerto " + SERVER_PORT);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException ex) {
            System.err.println("Error en el servidor: " + ex.getMessage());
        }
    }

    /**
     * Método que reenvía el mensaje recibido a todos los clientes registrados.
     *
     * @param message El mensaje a reenviar.
     */
    public static void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientInfo client : clients) {
                try (Socket socket = new Socket(client.getIp(), client.getPort());
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println(message);
                } catch (IOException ex) {
                    System.err.println("Error enviando mensaje a " + client.getIp() + ":" + client.getPort() + " - " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Registra un nuevo cliente.
     *
     * @param ip    La IP del cliente.
     * @param port  El puerto en el que el cliente escuchará.
     */
    public static void registerClient(String ip, int port) {
        synchronized (clients) {
            // Evitar registros duplicados
            boolean exists = clients.stream().anyMatch(c -> c.getIp().equals(ip) && c.getPort() == port);
            if (!exists) {
                clients.add(new ClientInfo(ip, port));
                System.out.println("Cliente registrado: " + ip + ":" + port);
            }
        }
    }
}

/**
 * Clase para almacenar la información de un cliente.
 */
class ClientInfo {
    private final String ip;
    private final int port;

    public ClientInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}

/**
 * Runnable que gestiona cada conexión entrante al servidor central.
 * Dependiendo del mensaje recibido, se procesa el registro o el envío de mensajes.
 */
class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String clientIp = socket.getInetAddress().getHostAddress();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine = in.readLine();
            if (inputLine != null) {
                if (inputLine.startsWith("REGISTER:")) {
                    // Registro de cliente. El mensaje debe tener el formato: REGISTER:<puerto>
                    int clientPort = Integer.parseInt(inputLine.substring("REGISTER:".length()).trim());
                    CentralServer.registerClient(clientIp, clientPort);
                } else if (inputLine.startsWith("MESSAGE:")) {
                    // Se recibió un mensaje para retransmitir.
                    String message = inputLine.substring("MESSAGE:".length()).trim();
                    System.out.println("Mensaje recibido de " + clientIp + ": " + message);
                    CentralServer.broadcastMessage("[" + clientIp + "]: " + message);
                } else {
                    System.out.println("Mensaje desconocido de " + clientIp + ": " + inputLine);
                }
            }
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Error en la conexión con " + clientIp + ": " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Se ignora el error al cerrar el socket.
            }
        }
    }
}

package org.example.socket.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 */

public class TcpServer {
    public static void main(String[] args) {
        int puerto = 12000; // Puerto superior a 10000

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor TCP escuchando en el puerto " + puerto);

            // Aceptar conexión del cliente
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + socket.getInetAddress());

            // Leer mensaje enviado por el cliente
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensajeRecibido = input.readLine();
            System.out.println("Mensaje recibido: " + mensajeRecibido);

            // Responder al cliente
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("Mensaje recibido: " + mensajeRecibido);

            // Cerrar conexión
            socket.close();
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
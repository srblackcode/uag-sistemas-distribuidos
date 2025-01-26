package org.example.socket.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 */

public class TcpClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Solicitar IP y puerto del servidor
            System.out.print("Ingresa la IP del servidor (e.g., 127.0.0.1): ");
            String ipServidor = scanner.nextLine();

            System.out.print("Ingresa el puerto del servidor: ");
            int puertoServidor = Integer.parseInt(scanner.nextLine());

            // Conexión al servidor
            try (Socket socket = new Socket(ipServidor, puertoServidor)) {
                System.out.println("Conectado al servidor");

                // Enviar mensaje
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                String mensaje = "¡¡¡HOLA MUNDO!!!";
                output.println(mensaje);
                System.out.println("Cliente: mensaje enviado -> " + mensaje);

                // Leer respuesta del servidor
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String respuesta = input.readLine();
                System.out.println("Servidor respondió: " + respuesta);
            }
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
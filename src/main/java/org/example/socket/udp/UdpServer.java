package org.example.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * UAG
 * Guillermo Omar Martinez Toledo
 */

public class UdpServer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Solicitar IP y puerto al usuario (opcional, se puede hardcodear)
            System.out.print("Ingresa la IP del servidor (e.g., 127.0.0.1): ");
            String ipServidor = scanner.nextLine();

            System.out.print("Ingresa el puerto para el servidor (mayor a 10000): ");
            int puertoServidor = Integer.parseInt(scanner.nextLine());

            InetAddress address = InetAddress.getByName(ipServidor);

            // Se crea un socket en la IP y puerto indicados
            try (DatagramSocket socketServidor = new DatagramSocket(puertoServidor, address)) {
                System.out.println("Servidor UDP escuchando en " + ipServidor + ":" + puertoServidor);

                // Buffer para recibir datagramas
                byte[] buffer = new byte[1024];

                // Se mantiene a la escucha de un solo mensaje en este ejemplo
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);

                // Espera a recibir el paquete
                socketServidor.receive(paqueteRecibido);

                // Extraer datos del paquete
                String mensaje = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());
                System.out.println("Servidor recibió: " + mensaje);

                // Aquí podríamos agregar lógica extra, como responder al cliente
            }

        } catch (IOException e) {
            System.err.println("Error de E/S en el servidor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("El puerto debe ser un número válido.");
        } finally {
            scanner.close();
        }
    }
}
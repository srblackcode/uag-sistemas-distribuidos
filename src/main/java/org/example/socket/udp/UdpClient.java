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

public class UdpClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Solicitar IP y puerto al usuario (coincidir con los usados por el servidor)
            System.out.print("Ingresa la IP del servidor (e.g., 127.0.0.1): ");
            String ipServidor = scanner.nextLine();

            System.out.print("Ingresa el puerto del servidor: ");
            int puertoServidor = Integer.parseInt(scanner.nextLine());

            // Creación de un socket para envío (no requiere especificar IP local en la mayoría de casos)
            try (DatagramSocket socketCliente = new DatagramSocket()) {

                // Mensaje a enviar
                String mensaje = "¡¡¡HOLA MUNDO!!!";

                // Convertir el mensaje a bytes
                byte[] bufferEnvio = mensaje.getBytes();

                InetAddress address = InetAddress.getByName(ipServidor);

                // Empaquetar datagrama para enviarlo al servidor
                DatagramPacket paqueteEnvio = new DatagramPacket(
                        bufferEnvio,
                        bufferEnvio.length,
                        address,
                        puertoServidor
                );

                // Envío del paquete
                socketCliente.send(paqueteEnvio);
                System.out.println("Cliente: mensaje enviado -> " + mensaje);
            }

        } catch (IOException e) {
            System.err.println("Error de E/S en el cliente: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("El puerto debe ser un número válido.");
        } finally {
            scanner.close();
        }
    }
}

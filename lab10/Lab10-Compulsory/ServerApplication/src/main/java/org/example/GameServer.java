package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private static final int SERVER_PORT = 1234;
    public static volatile boolean serverShouldStop = false;

    public static void main(String[] args) {
        System.out.println("Server pornit pe portul " + SERVER_PORT);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (!serverShouldStop) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client conectat: " + clientSocket.getInetAddress());

                ClientThread clientHandler = new ClientThread(clientSocket);
                clientHandler.start();
            }
            System.out.println("Server oprit.");
        } catch (IOException exception) {
            if (serverShouldStop) {
                System.out.println("Server oprit.");
            } else {
                System.out.println("Eroare la pornirea serverului: " + exception.getMessage());
            }
        }
    }
}
package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket clientSocket;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader inputFromClient = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outputToClient = new PrintWriter(
                        clientSocket.getOutputStream(), true)
        ) {
            String receivedCommand;
            while ((receivedCommand = inputFromClient.readLine()) != null) {
                System.out.println("Comanda primita: " + receivedCommand);

                if (receivedCommand.equalsIgnoreCase("stop")) {
                    outputToClient.println("Server stopped");
                    GameServer.serverShouldStop = true;
                    System.exit(0);
                } else {
                    outputToClient.println("Server received the request " + receivedCommand);
                }
            }

            clientSocket.close();
            System.out.println("Client deconectat.");
        } catch (IOException exception) {
            System.out.println("Eroare in ClientThread: " + exception.getMessage());
        }
    }
}
package org.example.client;

import org.example.client.network.ServerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        try (
                Socket connectionToServer = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader inputFromServer = new BufferedReader(
                        new InputStreamReader(connectionToServer.getInputStream()));
                PrintWriter outputToServer = new PrintWriter(
                        connectionToServer.getOutputStream(), true);
                Scanner keyboardInput = new Scanner(System.in)
        ) {
            ServerListener serverListener = new ServerListener(inputFromServer);
            Thread listenerThread = new Thread(serverListener);
            listenerThread.start();

            while (true) {
                String userCommand = keyboardInput.nextLine();

                if (serverListener.hasServerConnectionBeenLost()) {
                    System.out.println("Server-ul nu mai este disponibil. Client inchis.");
                    break;
                }

                if (userCommand.equalsIgnoreCase("exit")) {
                    System.out.println("Client inchis.");
                    serverListener.stopListening();
                    break;
                }

                outputToServer.println(userCommand);
            }
        } catch (IOException exception) {
            System.out.println("Eroare la conectarea cu serverul: " + exception.getMessage());
        }

        System.exit(0);
    }
}
package org.example;

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
            System.out.println("Conectat la server. Scrie comenzi (sau 'exit' pentru iesire):");

            while (true) {
                String userCommand = keyboardInput.nextLine();

                if (userCommand.equalsIgnoreCase("exit")) {
                    System.out.println("Client inchis.");
                    break;
                }

                outputToServer.println(userCommand);

                String serverResponse = inputFromServer.readLine();
                System.out.println("Raspuns server: " + serverResponse);

                if (userCommand.equalsIgnoreCase("stop")) {
                    break;
                }
            }
        } catch (IOException exception) {
            System.out.println("Eroare la conectarea cu serverul: " + exception.getMessage());
        }
    }
}
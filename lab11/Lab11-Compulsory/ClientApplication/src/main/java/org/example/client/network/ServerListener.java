package org.example.client.network;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerListener implements Runnable {
    private BufferedReader inputFromServer;
    private volatile boolean isListening;
    private volatile boolean serverConnectionLost;

    public ServerListener(BufferedReader inputFromServer) {
        this.inputFromServer = inputFromServer;
        this.isListening = true;
        this.serverConnectionLost = false;
    }

    @Override
    public void run() {
        try {
            String messageFromServer;
            while (isListening && (messageFromServer = inputFromServer.readLine()) != null) {
                System.out.println(messageFromServer);
            }
        } catch (IOException exception) {
            if (isListening) {
                System.out.println("Conexiune cu serverul intrerupta.");
            }
        } finally {
            serverConnectionLost = true;
            System.out.println("Apasa Enter pentru a inchide clientul.");
        }
    }

    public void stopListening() {
        this.isListening = false;
    }

    public boolean hasServerConnectionBeenLost() {
        return serverConnectionLost;
    }
}
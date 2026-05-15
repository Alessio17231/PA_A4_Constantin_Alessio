package org.example.server;

import org.example.io.QuestionLoader;
import org.example.model.GameState;
import org.example.model.Player;
import org.example.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private static final int SERVER_PORT = 1234;
    private static final int MAX_CONCURRENT_PLAYERS = 10;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final String QUESTIONS_FILE_PATH = "questions.txt";

    private static ExecutorService clientThreadPool;
    private static ServerSocket serverSocket;
    private static GameState gameState;
    private static volatile boolean serverIsRunning = true;

    public static void main(String[] args) {
        clientThreadPool = Executors.newFixedThreadPool(MAX_CONCURRENT_PLAYERS);

        try {
            QuestionLoader questionLoader = new QuestionLoader(QUESTIONS_FILE_PATH);
            List<Question> loadedQuestions = questionLoader.loadQuestions();
            System.out.println("S-au incarcat " + loadedQuestions.size() + " intrebari.");

            gameState = new GameState(loadedQuestions);

            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server pornit pe portul " + SERVER_PORT);
            System.out.println("Se asteapta " + gameState.getRequiredPlayersCount() + " jucatori...");
            System.out.println("Scrie 'shutdown' in aceasta consola pentru a opri serverul.");

            startShutdownListener();
            acceptClientsLoop();

        } catch (IOException exception) {
            if (serverIsRunning) {
                System.out.println("Eroare la server: " + exception.getMessage());
            }
        }

        System.out.println("Server inchis complet.");
    }

    private static void startShutdownListener() {
        Thread shutdownListenerThread = new Thread(() -> {
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            try {
                String adminCommand;
                while ((adminCommand = consoleInput.readLine()) != null) {
                    if (adminCommand.equalsIgnoreCase("shutdown")) {
                        initiateShutdown();
                        break;
                    }
                }
            } catch (IOException exception) {
                System.out.println("Eroare la citirea comenzilor admin: " + exception.getMessage());
            }
        });
        shutdownListenerThread.setDaemon(true);
        shutdownListenerThread.start();
    }

    private static void acceptClientsLoop() throws IOException {
        while (serverIsRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexiune noua: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, gameState);
                clientThreadPool.submit(clientHandler);
            } catch (IOException exception) {
                if (!serverIsRunning) {
                    break;
                }
                throw exception;
            }
        }
    }

    private static void initiateShutdown() {
        System.out.println("\nIncepe oprirea serverului...");
        serverIsRunning = false;

        notifyAllPlayers();
        shutdownThreadPool();
        closeServerSocket();
    }

    private static void notifyAllPlayers() {
        System.out.println("Se anunta jucatorii conectati...");
        for (Player connectedPlayer : gameState.getConnectedPlayers()) {
            connectedPlayer.sendMessage("Server se opreste. Multumim pentru joc!");
        }
    }

    private static void shutdownThreadPool() {
        System.out.println("Se opreste thread pool-ul...");
        clientThreadPool.shutdown();
        try {
            boolean terminatedInTime = clientThreadPool.awaitTermination(
                    SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!terminatedInTime) {
                System.out.println("Timp expirat, se forteaza oprirea thread-urilor...");
                clientThreadPool.shutdownNow();
            }
        } catch (InterruptedException exception) {
            clientThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket inchis.");
            }
        } catch (IOException exception) {
            System.out.println("Eroare la inchiderea socketului: " + exception.getMessage());
        }
    }
}
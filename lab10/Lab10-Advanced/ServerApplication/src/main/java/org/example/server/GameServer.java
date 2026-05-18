package org.example.server;

import org.example.io.QuestionLoader;
import org.example.model.GameState;
import org.example.model.Player;
import org.example.model.Question;
import org.example.strategy.CustomLearningBotStrategy;
import org.example.strategy.LLMBotStrategy;
import org.example.strategy.PlayerStrategy;
import org.example.strategy.RandomBotStrategy;

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
    private static final int MAX_CONCURRENT_PLAYERS = 100;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final String QUESTIONS_FILE_PATH = "questions.txt";
    private static final String GEMINI_API_KEY = "AIzaSyAgzvcqmJ5dyRhj4JPeqpJSsEU5XSBoaz4";

    private static ExecutorService clientThreadPool;
    private static ServerSocket serverSocket;
    private static GameState gameState;
    private static volatile boolean serverIsRunning = true;
    private static int botCounter = 0;

    public static void main(String[] args) {
        clientThreadPool = Executors.newFixedThreadPool(MAX_CONCURRENT_PLAYERS);

        try {
            QuestionLoader questionLoader = new QuestionLoader(QUESTIONS_FILE_PATH);
            List<Question> loadedQuestions = questionLoader.loadQuestions();
            System.out.println("S-au incarcat " + loadedQuestions.size() + " intrebari.");

            gameState = new GameState(loadedQuestions);

            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server pornit pe portul " + SERVER_PORT);
            System.out.println("Comenzi admin: 'addbot random' | 'addbot custom' | 'addbot llm easy|medium|hard' | 'shutdown'");

            startAdminListener();
            acceptClientsLoop();

        } catch (IOException exception) {
            if (serverIsRunning) {
                System.out.println("Eroare la server: " + exception.getMessage());
            }
        }

        System.out.println("Server inchis complet.");
    }

    private static void startAdminListener() {
        Thread adminThread = new Thread(() -> {
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            try {
                String adminCommand;
                while ((adminCommand = consoleInput.readLine()) != null) {
                    if (adminCommand.equalsIgnoreCase("shutdown")) {
                        initiateShutdown();
                        break;
                    } else if (adminCommand.toLowerCase().startsWith("addbot")) {
                        handleAddBotCommand(adminCommand);
                    }
                }
            } catch (IOException exception) {
                System.out.println("Eroare admin: " + exception.getMessage());
            }
        });
        adminThread.setDaemon(true);
        adminThread.start();
    }

    private static void handleAddBotCommand(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            System.out.println("Uz: addbot random | addbot custom | addbot llm easy|medium|hard");
            return;
        }

        String botType = parts[1].toLowerCase();
        PlayerStrategy botStrategy;
        String botName;

        switch (botType) {
            case "random":
                botStrategy = new RandomBotStrategy();
                botName = "RandomBot_" + (++botCounter);
                break;
            case "custom":
                botStrategy = new CustomLearningBotStrategy();
                botName = "CustomBot_" + (++botCounter);
                break;
            case "llm":
                LLMBotStrategy.Difficulty difficulty = LLMBotStrategy.Difficulty.MEDIUM;
                if (parts.length >= 3) {
                    try {
                        difficulty = LLMBotStrategy.Difficulty.valueOf(parts[2].toUpperCase());
                    } catch (IllegalArgumentException ignored) {}
                }
                botStrategy = new LLMBotStrategy(GEMINI_API_KEY, difficulty);
                botName = "LLMBot_" + difficulty + "_" + (++botCounter);
                break;
            default:
                System.out.println("Tip bot necunoscut: " + botType);
                return;
        }

        Player botPlayer = new Player(botName, null, null, null, botStrategy);
        gameState.addPlayer(botPlayer);
        System.out.println("Bot adaugat: " + botName);
        clientThreadPool.submit(new BotHandler(botPlayer, gameState));
    }

    private static void acceptClientsLoop() throws IOException {
        while (serverIsRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexiune noua: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, gameState);
                clientThreadPool.submit(clientHandler);
            } catch (IOException exception) {
                if (!serverIsRunning) break;
                throw exception;
            }
        }
    }

    private static void initiateShutdown() {
        System.out.println("\nIncepe oprirea serverului...");
        serverIsRunning = false;
        for (Player player : gameState.getConnectedPlayers()) {
            player.sendMessage("Server se opreste.");
        }
        clientThreadPool.shutdown();
        try {
            if (!clientThreadPool.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                clientThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            clientThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException ignored) {}
    }
}
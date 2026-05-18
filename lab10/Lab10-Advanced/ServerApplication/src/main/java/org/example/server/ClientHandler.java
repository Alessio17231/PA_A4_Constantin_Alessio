package org.example.server;

import org.example.model.GameState;
import org.example.model.Player;
import org.example.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.example.strategy.HumanPlayerStrategy;
import org.example.strategy.PlayerStrategy;

import org.example.strategy.CustomLearningBotStrategy;

public class ClientHandler implements Runnable {
    private static final int ANSWER_TIMEOUT_MILLIS = 10000;

    private Socket clientSocket;
    private GameState gameState;

    public ClientHandler(Socket clientSocket, GameState gameState) {
        this.clientSocket = clientSocket;
        this.gameState = gameState;
    }

    @Override
    public void run() {
        try {
            BufferedReader inputFromClient = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(
                    clientSocket.getOutputStream(), true);

            Player currentPlayer = registerPlayer(inputFromClient, outputToClient);
            waitForGameStart(currentPlayer);
            playAllQuestions(currentPlayer);
            announceFinalResults(currentPlayer);

            clientSocket.close();
            System.out.println("Jucator deconectat: " + currentPlayer.getPlayerName());
        } catch (IOException | InterruptedException exception) {
            System.out.println("Eroare in ClientHandler: " + exception.getMessage());
        }
    }

    private Player registerPlayer(BufferedReader inputFromClient, PrintWriter outputToClient)
            throws IOException {
        outputToClient.println("Bun venit! Introdu numele tau:");
        String playerName = inputFromClient.readLine();

        PlayerStrategy humanStrategy = new HumanPlayerStrategy(inputFromClient);
        Player newPlayer = new Player(playerName, clientSocket, inputFromClient,
                outputToClient, humanStrategy);
        gameState.addPlayer(newPlayer);

        System.out.println("Jucator inregistrat: " + playerName);
        newPlayer.sendMessage("Salut, " + playerName + "! Astepti alti jucatori...");
        return newPlayer;
    }

    private void waitForGameStart(Player currentPlayer) throws InterruptedException {
        gameState.waitForAllPlayers();
        currentPlayer.sendMessage("Toti jucatorii sunt conectati. Jocul incepe!");
        currentPlayer.sendMessage("Ai " + (ANSWER_TIMEOUT_MILLIS / 1000)
                + " secunde la fiecare intrebare.");
    }

    private void playAllQuestions(Player currentPlayer) throws IOException {
        int questionNumber = 1;
        for (Question currentQuestion : gameState.getGameQuestions()) {
            currentPlayer.sendMessage("\n--- Intrebarea " + questionNumber + " ---");
            currentPlayer.sendMessage(currentQuestion.formatForDisplay());
            currentPlayer.sendMessage("Raspunsul tau (A/B/C/D):");

            String givenAnswer = readAnswerWithTimeout(currentPlayer, currentQuestion);
            evaluateAnswer(currentPlayer, currentQuestion, givenAnswer);

            questionNumber++;
        }
    }

    private String readAnswerWithTimeout(Player currentPlayer, Question currentQuestion)
            throws IOException {
        clientSocket.setSoTimeout(ANSWER_TIMEOUT_MILLIS);
        long questionStartTime = System.currentTimeMillis();

        try {
            String answer = currentPlayer.getPlayerStrategy().chooseAnswer(currentQuestion);
            long responseTime = System.currentTimeMillis() - questionStartTime;
            currentPlayer.addResponseTime(responseTime);
            return answer;
        } catch (SocketTimeoutException timeoutException) {
            currentPlayer.addResponseTime(ANSWER_TIMEOUT_MILLIS);
            currentPlayer.sendMessage("Timp expirat!");
            return null;
        } catch (Exception strategyException) {
            currentPlayer.addResponseTime(ANSWER_TIMEOUT_MILLIS);
            System.out.println("Eroare la strategie: " + strategyException.getMessage());
            return null;
        } finally {
            clientSocket.setSoTimeout(0);
        }
    }

    private void evaluateAnswer(Player currentPlayer, Question currentQuestion, String givenAnswer) {
        if (givenAnswer != null && currentQuestion.isAnswerCorrect(givenAnswer)) {
            currentPlayer.incrementCorrectAnswers();
            currentPlayer.sendMessage("Corect!");
        } else {
            currentPlayer.sendMessage("Gresit! Raspuns corect: "
                    + currentQuestion.getCorrectAnswerLetter());
        }

        if (currentPlayer.getPlayerStrategy() instanceof CustomLearningBotStrategy customBot) {
            customBot.learnFromQuestion(currentQuestion);
        }
    }

    private void announceFinalResults(Player currentPlayer) {
        currentPlayer.sendMessage("\n=== JOC TERMINAT ===");
        currentPlayer.sendMessage("Scor final: " + currentPlayer.getCorrectAnswersCount()
                + " raspunsuri corecte in " + currentPlayer.getTotalResponseTimeMillis() + " ms");

        currentPlayer.sendMessage("\nClasament:");
        int rank = 1;
        for (Player rankedPlayer : gameState.getRankedPlayers()) {
            currentPlayer.sendMessage(rank + ". " + rankedPlayer.getPlayerName()
                    + " - " + rankedPlayer.getCorrectAnswersCount() + " corecte, "
                    + rankedPlayer.getTotalResponseTimeMillis() + " ms");
            rank++;
        }
    }
}
package org.example.model;

import org.example.strategy.PlayerStrategy;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player {
    private String playerName;
    private Socket playerSocket;
    private BufferedReader inputFromPlayer;
    private PrintWriter outputToPlayer;
    private PlayerStrategy playerStrategy;
    private int correctAnswersCount;
    private long totalResponseTimeMillis;

    public Player(String playerName, Socket playerSocket,
                  BufferedReader inputFromPlayer, PrintWriter outputToPlayer,
                  PlayerStrategy playerStrategy) {
        this.playerName = playerName;
        this.playerSocket = playerSocket;
        this.inputFromPlayer = inputFromPlayer;
        this.outputToPlayer = outputToPlayer;
        this.playerStrategy = playerStrategy;
        this.correctAnswersCount = 0;
        this.totalResponseTimeMillis = 0;
    }

    public String getPlayerName() { return playerName; }
    public Socket getPlayerSocket() { return playerSocket; }
    public BufferedReader getInputFromPlayer() { return inputFromPlayer; }
    public PrintWriter getOutputToPlayer() { return outputToPlayer; }
    public PlayerStrategy getPlayerStrategy() { return playerStrategy; }
    public int getCorrectAnswersCount() { return correctAnswersCount; }
    public long getTotalResponseTimeMillis() { return totalResponseTimeMillis; }

    public void incrementCorrectAnswers() { this.correctAnswersCount++; }
    public void addResponseTime(long responseTimeMillis) { this.totalResponseTimeMillis += responseTimeMillis; }

    public void sendMessage(String message) {
        if (outputToPlayer != null) {
            outputToPlayer.println(message);
        }
    }

    public boolean isHumanPlayer() {
        return playerSocket != null;
    }
}
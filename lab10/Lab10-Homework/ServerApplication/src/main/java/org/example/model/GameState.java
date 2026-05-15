package org.example.model;

import org.example.model.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GameState {
    private static final int REQUIRED_PLAYERS_COUNT = 2;

    private List<Question> gameQuestions;
    private List<Player> connectedPlayers;
    private CountDownLatch playersReadyLatch;

    public GameState(List<Question> gameQuestions) {
        this.gameQuestions = gameQuestions;
        this.connectedPlayers = new ArrayList<>();
        this.playersReadyLatch = new CountDownLatch(REQUIRED_PLAYERS_COUNT);
    }

    public synchronized void addPlayer(Player newPlayer) {
        connectedPlayers.add(newPlayer);
        playersReadyLatch.countDown();
    }

    public void waitForAllPlayers() throws InterruptedException {
        playersReadyLatch.await();
    }

    public List<Question> getGameQuestions() {
        return gameQuestions;
    }

    public List<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public int getRequiredPlayersCount() {
        return REQUIRED_PLAYERS_COUNT;
    }

    public synchronized int getCurrentPlayersCount() {
        return connectedPlayers.size();
    }

    public List<Player> getRankedPlayers() {
        List<Player> sortedPlayers = new ArrayList<>(connectedPlayers);
        sortedPlayers.sort(
                Comparator.comparingInt(Player::getCorrectAnswersCount).reversed()
                        .thenComparingLong(Player::getTotalResponseTimeMillis)
        );
        return sortedPlayers;
    }
}
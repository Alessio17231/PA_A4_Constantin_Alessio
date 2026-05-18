package org.example.server;

import org.example.model.GameState;
import org.example.model.Player;
import org.example.model.Question;
import org.example.strategy.CustomLearningBotStrategy;

public class BotHandler implements Runnable {
    private Player botPlayer;
    private GameState gameState;

    public BotHandler(Player botPlayer, GameState gameState) {
        this.botPlayer = botPlayer;
        this.gameState = gameState;
    }

    @Override
    public void run() {
        try {
            gameState.waitForAllPlayers();

            for (Question currentQuestion : gameState.getGameQuestions()) {
                long startTime = System.currentTimeMillis();
                String botAnswer;
                try {
                    botAnswer = botPlayer.getPlayerStrategy().chooseAnswer(currentQuestion);
                } catch (Exception exception) {
                    botAnswer = null;
                }
                long responseTime = System.currentTimeMillis() - startTime;
                botPlayer.addResponseTime(responseTime);

                if (botAnswer != null && currentQuestion.isAnswerCorrect(botAnswer)) {
                    botPlayer.incrementCorrectAnswers();
                }

                if (botPlayer.getPlayerStrategy() instanceof CustomLearningBotStrategy customBot) {
                    customBot.learnFromQuestion(currentQuestion);
                }
            }

            System.out.println("Bot " + botPlayer.getPlayerName() + " a terminat: "
                    + botPlayer.getCorrectAnswersCount() + " corecte, "
                    + botPlayer.getTotalResponseTimeMillis() + " ms");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
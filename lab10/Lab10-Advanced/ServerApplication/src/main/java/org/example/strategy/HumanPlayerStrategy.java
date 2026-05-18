package org.example.strategy;

import org.example.model.Question;

import java.io.BufferedReader;
import java.io.IOException;

public class HumanPlayerStrategy implements PlayerStrategy {
    private BufferedReader inputFromPlayer;

    public HumanPlayerStrategy(BufferedReader inputFromPlayer) {
        this.inputFromPlayer = inputFromPlayer;
    }

    @Override
    public String chooseAnswer(Question currentQuestion) throws IOException {
        return inputFromPlayer.readLine();
    }
}
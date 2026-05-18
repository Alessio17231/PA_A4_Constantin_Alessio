package org.example.strategy;

import org.example.model.Question;

import java.util.Random;

public class RandomBotStrategy implements PlayerStrategy {
    private static final String[] POSSIBLE_ANSWERS = {"A", "B", "C", "D"};
    private Random randomGenerator;

    public RandomBotStrategy() {
        this.randomGenerator = new Random();
    }

    @Override
    public String chooseAnswer(Question currentQuestion) {
        int randomIndex = randomGenerator.nextInt(POSSIBLE_ANSWERS.length);
        return POSSIBLE_ANSWERS[randomIndex];
    }
}
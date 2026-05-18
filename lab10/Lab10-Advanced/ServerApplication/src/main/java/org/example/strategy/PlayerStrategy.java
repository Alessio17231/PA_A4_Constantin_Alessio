package org.example.strategy;

import org.example.model.Question;

public interface PlayerStrategy {
    String chooseAnswer(Question currentQuestion) throws Exception;
}
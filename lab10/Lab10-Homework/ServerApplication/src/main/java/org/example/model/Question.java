package org.example.model;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> answerOptions;
    private String correctAnswerLetter;

    public Question(String questionText, List<String> answerOptions, String correctAnswerLetter) {
        this.questionText = questionText;
        this.answerOptions = answerOptions;
        this.correctAnswerLetter = correctAnswerLetter;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAnswerOptions() {
        return answerOptions;
    }

    public String getCorrectAnswerLetter() {
        return correctAnswerLetter;
    }

    public boolean isAnswerCorrect(String givenAnswer) {
        return correctAnswerLetter.equalsIgnoreCase(givenAnswer);
    }

    public String formatForDisplay() {
        StringBuilder formatted = new StringBuilder();
        formatted.append(questionText).append("\n");
        for (String option : answerOptions) {
            formatted.append(option).append("\n");
        }
        return formatted.toString();
    }
}
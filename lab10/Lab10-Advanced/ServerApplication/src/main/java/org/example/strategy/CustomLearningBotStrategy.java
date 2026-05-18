package org.example.strategy;

import org.example.model.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomLearningBotStrategy implements PlayerStrategy {
    private static final String KNOWLEDGE_BASE_FILE = "bot_knowledge.txt";
    private static final String[] POSSIBLE_ANSWERS = {"A", "B", "C", "D"};

    private Map<String, String> learnedAnswers;
    private Random randomGenerator;

    public CustomLearningBotStrategy() {
        this.learnedAnswers = new HashMap<>();
        this.randomGenerator = new Random();
        loadKnowledgeFromFile();
    }

    @Override
    public String chooseAnswer(Question currentQuestion) {
        String questionKey = currentQuestion.getQuestionText();
        if (learnedAnswers.containsKey(questionKey)) {
            return learnedAnswers.get(questionKey);
        }
        return POSSIBLE_ANSWERS[randomGenerator.nextInt(POSSIBLE_ANSWERS.length)];
    }

    public void learnFromQuestion(Question answeredQuestion) {
        learnedAnswers.put(answeredQuestion.getQuestionText(),
                answeredQuestion.getCorrectAnswerLetter());
        saveKnowledgeToFile();
    }

    private void loadKnowledgeFromFile() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(KNOWLEDGE_BASE_FILE))) {
            String currentLine;
            while ((currentLine = fileReader.readLine()) != null) {
                String[] parts = currentLine.split("\\|");
                if (parts.length == 2) {
                    learnedAnswers.put(parts[0], parts[1]);
                }
            }
            System.out.println("Bot a invatat " + learnedAnswers.size() + " raspunsuri din istoric.");
        } catch (IOException ignored) {
            System.out.println("Knowledge base nou (fisier inexistent).");
        }
    }

    private void saveKnowledgeToFile() {
        try (PrintWriter fileWriter = new PrintWriter(new FileWriter(KNOWLEDGE_BASE_FILE))) {
            for (Map.Entry<String, String> entry : learnedAnswers.entrySet()) {
                fileWriter.println(entry.getKey() + "|" + entry.getValue());
            }
        } catch (IOException exception) {
            System.out.println("Eroare salvare knowledge: " + exception.getMessage());
        }
    }
}
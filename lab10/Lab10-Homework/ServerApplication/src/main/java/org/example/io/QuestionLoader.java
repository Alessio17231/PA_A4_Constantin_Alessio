package org.example.io;

import org.example.model.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionLoader {
    private String filePath;

    public QuestionLoader(String filePath) {
        this.filePath = filePath;
    }

    public List<Question> loadQuestions() throws IOException {
        List<Question> loadedQuestions = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            String questionText = null;
            List<String> currentOptions = new ArrayList<>();

            while ((currentLine = fileReader.readLine()) != null) {
                currentLine = currentLine.trim();

                if (currentLine.equals("---")) {
                    continue;
                }

                if (questionText == null) {
                    questionText = currentLine;
                } else if (currentOptions.size() < 4) {
                    currentOptions.add(currentLine);
                } else {
                    String correctAnswer = currentLine;
                    loadedQuestions.add(new Question(questionText, new ArrayList<>(currentOptions), correctAnswer));
                    questionText = null;
                    currentOptions.clear();
                }
            }
        }

        return loadedQuestions;
    }
}
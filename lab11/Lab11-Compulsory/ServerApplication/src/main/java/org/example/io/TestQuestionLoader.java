package org.example.io;

import org.example.model.Question;

import java.io.IOException;
import java.util.List;

public class TestQuestionLoader {
    public static void main(String[] args) {
        QuestionLoader loader = new QuestionLoader("questions.txt");

        try {
            List<Question> questions = loader.loadQuestions();
            System.out.println("S-au incarcat " + questions.size() + " intrebari:\n");

            for (int index = 0; index < questions.size(); index++) {
                System.out.println("Intrebarea " + (index + 1) + ":");
                System.out.println(questions.get(index).formatForDisplay());
                System.out.println("Raspuns corect: " + questions.get(index).getCorrectAnswerLetter());
                System.out.println();
            }
        } catch (IOException exception) {
            System.out.println("Eroare la citirea fisierului: " + exception.getMessage());
        }
    }
}
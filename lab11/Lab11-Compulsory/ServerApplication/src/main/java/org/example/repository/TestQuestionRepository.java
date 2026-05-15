package org.example.repository;

import org.example.io.QuestionLoader;
import org.example.model.Question;

import java.io.IOException;
import java.util.List;

public class TestQuestionRepository {
    public static void main(String[] args) {
        QuestionRepository questionRepository = new QuestionRepository();

        try {
            long existingQuestionsCount = questionRepository.countQuestions();
            System.out.println("Intrebari deja existente in DB: " + existingQuestionsCount);

            if (existingQuestionsCount == 0) {
                System.out.println("\nSe incarca intrebari din fisier...");
                QuestionLoader questionLoader = new QuestionLoader("questions.txt");
                List<Question> questionsFromFile = questionLoader.loadQuestions();
                System.out.println("Citite din fisier: " + questionsFromFile.size() + " intrebari.");

                System.out.println("Se salveaza in baza de date...");
                questionRepository.saveAllQuestions(questionsFromFile);
                System.out.println("Salvate cu succes!");
            } else {
                System.out.println("DB-ul are deja intrebari, sarim peste seed.");
            }

            System.out.println("\n--- Citire din DB ---");
            List<Question> questionsFromDatabase = questionRepository.findAllQuestions();

            for (Question questionFromDatabase : questionsFromDatabase) {
                System.out.println("\nID: " + questionFromDatabase.getId());
                System.out.println(questionFromDatabase.formatForDisplay());
                System.out.println("Raspuns corect: " + questionFromDatabase.getCorrectAnswerLetter());
            }

            System.out.println("\nTotal in DB: " + questionsFromDatabase.size() + " intrebari.");

            System.out.println("\n--- Test cautare dupa ID ---");
            if (!questionsFromDatabase.isEmpty()) {
                Long firstQuestionId = questionsFromDatabase.get(0).getId();
                Question foundQuestion = questionRepository.findQuestionById(firstQuestionId);
                System.out.println("Gasit cu ID " + firstQuestionId + ": " + foundQuestion.getQuestionText());
            }

        } catch (IOException ioException) {
            System.out.println("Eroare la citirea fisierului: " + ioException.getMessage());
        } finally {
            questionRepository.close();
        }
    }
}
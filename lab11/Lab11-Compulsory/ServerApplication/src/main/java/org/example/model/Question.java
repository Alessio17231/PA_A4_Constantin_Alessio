package org.example.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text", nullable = false, length = 200)
    @OrderColumn(name = "option_order")
    private List<String> answerOptions;

    @Column(name = "correct_answer_letter", nullable = false, length = 1)
    private String correctAnswerLetter;

    public Question() {
    }

    public Question(String questionText, List<String> answerOptions, String correctAnswerLetter) {
        this.questionText = questionText;
        this.answerOptions = answerOptions;
        this.correctAnswerLetter = correctAnswerLetter;
    }

    public Long getId() {
        return id;
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
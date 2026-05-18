package org.example.strategy;

import org.example.model.Question;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class LLMBotStrategy implements PlayerStrategy {
    public enum Difficulty {
        EASY(0.3), MEDIUM(0.7), HARD(1.0);

        private final double llmUsageProbability;
        Difficulty(double probability) { this.llmUsageProbability = probability; }
        public double getLlmUsageProbability() { return llmUsageProbability; }
    }

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private static final String[] POSSIBLE_ANSWERS = {"A", "B", "C", "D"};

    private String apiKey;
    private Difficulty difficulty;
    private HttpClient httpClient;
    private Random randomGenerator;

    public LLMBotStrategy(String apiKey, Difficulty difficulty) {
        this.apiKey = apiKey;
        this.difficulty = difficulty;
        this.httpClient = HttpClient.newHttpClient();
        this.randomGenerator = new Random();
    }

    @Override
    public String chooseAnswer(Question currentQuestion) throws Exception {
        if (randomGenerator.nextDouble() > difficulty.getLlmUsageProbability()) {
            return POSSIBLE_ANSWERS[randomGenerator.nextInt(POSSIBLE_ANSWERS.length)];
        }
        return callGeminiApi(currentQuestion);
    }

    private String callGeminiApi(Question currentQuestion) throws Exception {
        String promptText = buildPrompt(currentQuestion);
        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\""
                + escapeJsonString(promptText) + "\"}]}]}";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());
        return extractAnswerFromResponse(httpResponse.body());
    }

    private String buildPrompt(Question currentQuestion) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Raspunde la intrebare cu o singura litera (A, B, C sau D). ");
        prompt.append("Intrebare: ").append(currentQuestion.getQuestionText()).append(" ");
        for (String option : currentQuestion.getAnswerOptions()) {
            prompt.append(option).append(" ");
        }
        prompt.append("Raspuns (doar litera):");
        return prompt.toString();
    }

    private String escapeJsonString(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }

    private String extractAnswerFromResponse(String jsonResponse) {
        int textIndex = jsonResponse.indexOf("\"text\":");
        if (textIndex == -1) {
            return POSSIBLE_ANSWERS[randomGenerator.nextInt(POSSIBLE_ANSWERS.length)];
        }
        int valueStart = jsonResponse.indexOf("\"", textIndex + 7) + 1;
        int valueEnd = jsonResponse.indexOf("\"", valueStart);
        String rawAnswer = jsonResponse.substring(valueStart, valueEnd).trim().toUpperCase();
        for (String validAnswer : POSSIBLE_ANSWERS) {
            if (rawAnswer.contains(validAnswer)) {
                return validAnswer;
            }
        }
        return POSSIBLE_ANSWERS[randomGenerator.nextInt(POSSIBLE_ANSWERS.length)];
    }
}
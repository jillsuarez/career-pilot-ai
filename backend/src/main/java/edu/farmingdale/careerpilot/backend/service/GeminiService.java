package edu.farmingdale.careerpilot.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.farmingdale.careerpilot.backend.dto.GenerateRequest;
import edu.farmingdale.careerpilot.backend.model.ResumeProfile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class GeminiService {

    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiService(ObjectMapper objectMapper,
                         @Value("${gemini.api-key}") String apiKey,
                         @Value("${gemini.model}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateResume(ResumeProfile profile, GenerateRequest request) {
        String prompt = "Create a tailored resume draft for this student job seeker.\n\n"
                + resumeText(profile)
                + "\nJob title: " + safe(request.getJobTitle())
                + "\nCompany: " + safe(request.getCompany())
                + "\nJob description:\n" + safe(request.getJobDescription())
                + "\n\nKeep it clear, honest, and editable.";
        return callGemini(prompt);
    }

    public String generateCoverLetter(ResumeProfile profile, GenerateRequest request) {
        String prompt = "Create a cover letter draft for this student job seeker.\n\n"
                + resumeText(profile)
                + "\nJob title: " + safe(request.getJobTitle())
                + "\nCompany: " + safe(request.getCompany())
                + "\nJob description:\n" + safe(request.getJobDescription())
                + "\n\nKeep it professional, concise, and editable.";
        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is missing. Set GEMINI_API_KEY before starting the backend.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
        )));

        try {
            JsonNode response = RestClient.builder()
                    .baseUrl("https://generativelanguage.googleapis.com")
                    .defaultHeader("x-goog-api-key", apiKey)
                    .build()
                    .post()
                    .uri("/v1beta/models/" + model + ":generateContent")
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            String text = extractText(response);
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Gemini returned an empty response.");
            }
            return text.trim();
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException("Gemini request failed: " + exception.getResponseBodyAsString());
        }
    }

    private String extractText(JsonNode response) {
        if (response == null) {
            return "";
        }
        JsonNode outputText = response.get("output_text");
        if (outputText != null && outputText.isTextual()) {
            return outputText.asText();
        }
        JsonNode candidates = response.get("candidates");
        if (candidates != null && candidates.isArray() && !candidates.isEmpty()) {
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isArray()) {
                StringBuilder text = new StringBuilder();
                for (JsonNode part : parts) {
                    JsonNode partText = part.get("text");
                    if (partText != null && partText.isTextual()) {
                        text.append(partText.asText());
                    }
                }
                return text.toString();
            }
        }
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception exception) {
            return response.toString();
        }
    }

    private String resumeText(ResumeProfile profile) {
        return "Resume profile:"
                + "\nName: " + safe(profile.getFullName())
                + "\nEmail: " + safe(profile.getEmail())
                + "\nPhone: " + safe(profile.getPhone())
                + "\nEducation:\n" + safe(profile.getEducation())
                + "\nSkills:\n" + safe(profile.getSkills())
                + "\nExperience:\n" + safe(profile.getExperience());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}

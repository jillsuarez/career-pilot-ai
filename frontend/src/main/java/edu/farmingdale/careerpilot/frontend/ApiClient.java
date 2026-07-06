package edu.farmingdale.careerpilot.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.farmingdale.careerpilot.frontend.model.GenerateRequest;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.frontend.model.GenerationResponse;
import edu.farmingdale.careerpilot.frontend.model.ResumeProfile;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeProfile getResumeProfile() throws IOException, InterruptedException {
        return sendGet("/resume-profile", ResumeProfile.class);
    }

    public ResumeProfile saveResumeProfile(ResumeProfile profile) throws IOException, InterruptedException {
        return sendPost("/resume-profile", profile, ResumeProfile.class);
    }

    public GenerationResponse generateResume(GenerateRequest request) throws IOException, InterruptedException {
        return sendPost("/generate/resume", request, GenerationResponse.class);
    }

    public GenerationResponse generateCoverLetter(GenerateRequest request) throws IOException, InterruptedException {
        return sendPost("/generate/cover-letter", request, GenerationResponse.class);
    }

    public GeneratedDocument saveDocument(GeneratedDocument document) throws IOException, InterruptedException {
        return sendPost("/documents", document, GeneratedDocument.class);
    }

    public List<GeneratedDocument> getDocuments() throws IOException, InterruptedException {
        GeneratedDocument[] documents = sendGet("/documents", GeneratedDocument[].class);
        return Arrays.asList(documents);
    }

    public GeneratedDocument getDocument(String id) throws IOException, InterruptedException {
        return sendGet("/documents/" + id, GeneratedDocument.class);
    }

    private <T> T sendGet(String path, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();
        return send(request, responseType);
    }

    private <T> T sendPost(String path, Object body, Class<T> responseType) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return send(request, responseType);
    }

    private <T> T send(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), responseType);
        }
        throw new IOException(readErrorMessage(response.body()));
    }

    private String readErrorMessage(String body) {
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode message = node.get("message");
            if (message != null && message.isTextual()) {
                return message.asText();
            }
        } catch (Exception ignored) {
        }
        if (body == null || body.isBlank()) {
            return "Request failed.";
        }
        return body;
    }
}

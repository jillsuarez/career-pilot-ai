package edu.farmingdale.careerpilot.backend.dto;

public class GenerationResponse {

    private String documentType;
    private String content;

    public GenerationResponse() {
    }

    public GenerationResponse(String documentType, String content) {
        this.documentType = documentType;
        this.content = content;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

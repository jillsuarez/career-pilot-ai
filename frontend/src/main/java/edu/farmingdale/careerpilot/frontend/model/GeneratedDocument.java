package edu.farmingdale.careerpilot.frontend.model;

public class GeneratedDocument {

    private String id;
    private String documentType;
    private String jobTitle;
    private String company;
    private String jobDescription;
    private String content;
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        String type = documentType == null ? "document" : documentType;
        String title = jobTitle == null || jobTitle.isBlank() ? "Untitled role" : jobTitle;
        String employer = company == null || company.isBlank() ? "Unknown company" : company;
        return type + " - " + employer + " - " + title;
    }
}

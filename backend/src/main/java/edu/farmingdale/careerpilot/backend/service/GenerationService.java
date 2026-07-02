package edu.farmingdale.careerpilot.backend.service;

import edu.farmingdale.careerpilot.backend.dto.GenerateRequest;
import edu.farmingdale.careerpilot.backend.model.ResumeProfile;
import org.springframework.stereotype.Service;

@Service
public class GenerationService {

    private final ResumeProfileService resumeProfileService;
    private final GeminiService geminiService;

    public GenerationService(ResumeProfileService resumeProfileService, GeminiService geminiService) {
        this.resumeProfileService = resumeProfileService;
        this.geminiService = geminiService;
    }

    public String generateResume(GenerateRequest request) {
        validateRequest(request);
        ResumeProfile profile = resumeProfileService.getRequiredProfile();
        return geminiService.generateResume(profile, request);
    }

    public String generateCoverLetter(GenerateRequest request) {
        validateRequest(request);
        ResumeProfile profile = resumeProfileService.getRequiredProfile();
        return geminiService.generateCoverLetter(profile, request);
    }

    private void validateRequest(GenerateRequest request) {
        if (request == null || isBlank(request.getJobDescription())) {
            throw new IllegalArgumentException("Job description is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

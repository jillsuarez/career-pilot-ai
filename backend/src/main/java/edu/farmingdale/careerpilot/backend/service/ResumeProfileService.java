package edu.farmingdale.careerpilot.backend.service;

import edu.farmingdale.careerpilot.backend.model.ResumeProfile;
import edu.farmingdale.careerpilot.backend.repository.FirestoreRepository;
import org.springframework.stereotype.Service;

@Service
public class ResumeProfileService {

    private final FirestoreRepository firestoreRepository;

    public ResumeProfileService(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    public ResumeProfile getProfile() {
        return firestoreRepository.getProfile();
    }

    public ResumeProfile saveProfile(ResumeProfile profile) {
        return firestoreRepository.saveProfile(profile);
    }

    public ResumeProfile getRequiredProfile() {
        ResumeProfile profile = getProfile();
        if (isBlank(profile.getFullName()) || isBlank(profile.getEducation()) || isBlank(profile.getSkills())) {
            throw new IllegalArgumentException("Save your resume profile before generating a document.");
        }
        return profile;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

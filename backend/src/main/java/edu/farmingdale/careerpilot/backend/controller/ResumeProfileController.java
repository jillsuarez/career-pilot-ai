package edu.farmingdale.careerpilot.backend.controller;

import edu.farmingdale.careerpilot.backend.model.ResumeProfile;
import edu.farmingdale.careerpilot.backend.service.ResumeProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resume-profile")
public class ResumeProfileController {

    private final ResumeProfileService resumeProfileService;

    public ResumeProfileController(ResumeProfileService resumeProfileService) {
        this.resumeProfileService = resumeProfileService;
    }

    @GetMapping
    public ResumeProfile getProfile() {
        return resumeProfileService.getProfile();
    }

    @PostMapping
    public ResumeProfile saveProfile(@RequestBody ResumeProfile profile) {
        return resumeProfileService.saveProfile(profile);
    }
}

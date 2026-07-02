package edu.farmingdale.careerpilot.backend.controller;

import edu.farmingdale.careerpilot.backend.dto.GenerateRequest;
import edu.farmingdale.careerpilot.backend.dto.GenerationResponse;
import edu.farmingdale.careerpilot.backend.service.GenerationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generate")
public class GenerationController {

    private final GenerationService generationService;

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/resume")
    public GenerationResponse generateResume(@RequestBody GenerateRequest request) {
        return new GenerationResponse("resume", generationService.generateResume(request));
    }

    @PostMapping("/cover-letter")
    public GenerationResponse generateCoverLetter(@RequestBody GenerateRequest request) {
        return new GenerationResponse("cover letter", generationService.generateCoverLetter(request));
    }
}

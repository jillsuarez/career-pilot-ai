package edu.farmingdale.careerpilot.backend.controller;

import edu.farmingdale.careerpilot.backend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.backend.service.GeneratedDocumentService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class GeneratedDocumentController {

    private final GeneratedDocumentService generatedDocumentService;

    public GeneratedDocumentController(GeneratedDocumentService generatedDocumentService) {
        this.generatedDocumentService = generatedDocumentService;
    }

    @GetMapping
    public List<GeneratedDocument> getDocuments() {
        return generatedDocumentService.getDocuments();
    }

    @GetMapping("/{id}")
    public GeneratedDocument getDocument(@PathVariable String id) {
        return generatedDocumentService.getDocument(id);
    }

    @PostMapping
    public GeneratedDocument saveDocument(@RequestBody GeneratedDocument document) {
        return generatedDocumentService.saveDocument(document);
    }
}

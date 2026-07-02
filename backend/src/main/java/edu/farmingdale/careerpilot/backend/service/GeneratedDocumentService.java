package edu.farmingdale.careerpilot.backend.service;

import edu.farmingdale.careerpilot.backend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.backend.repository.FirestoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GeneratedDocumentService {

    private final FirestoreRepository firestoreRepository;

    public GeneratedDocumentService(FirestoreRepository firestoreRepository) {
        this.firestoreRepository = firestoreRepository;
    }

    public List<GeneratedDocument> getDocuments() {
        return firestoreRepository.getDocuments();
    }

    public GeneratedDocument getDocument(String id) {
        return firestoreRepository.getDocument(id);
    }

    public GeneratedDocument saveDocument(GeneratedDocument document) {
        if (isBlank(document.getDocumentType())) {
            throw new IllegalArgumentException("Document type is required.");
        }
        if (isBlank(document.getContent())) {
            throw new IllegalArgumentException("Document content is required.");
        }
        return firestoreRepository.saveDocument(document);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

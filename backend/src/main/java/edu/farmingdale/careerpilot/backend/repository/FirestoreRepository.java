package edu.farmingdale.careerpilot.backend.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import edu.farmingdale.careerpilot.backend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.backend.model.ResumeProfile;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Repository;

@Repository
public class FirestoreRepository {

    private static final String USER_ID = "demo-user";

    private final Firestore firestore;

    public FirestoreRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public ResumeProfile getProfile() {
        DocumentSnapshot snapshot = waitFor(profileDocument().get());
        if (!snapshot.exists()) {
            ResumeProfile profile = new ResumeProfile();
            profile.setId("current");
            return profile;
        }

        ResumeProfile profile = snapshot.toObject(ResumeProfile.class);
        if (profile == null) {
            profile = new ResumeProfile();
        }
        profile.setId(snapshot.getId());
        return profile;
    }

    public ResumeProfile saveProfile(ResumeProfile profile) {
        profile.setId("current");
        waitFor(profileDocument().set(profile));
        return profile;
    }

    public List<GeneratedDocument> getDocuments() {
        QuerySnapshot snapshot = waitFor(documentsCollection()
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get());

        List<GeneratedDocument> documents = new ArrayList<>();
        for (QueryDocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
            GeneratedDocument document = documentSnapshot.toObject(GeneratedDocument.class);
            document.setId(documentSnapshot.getId());
            documents.add(document);
        }
        return documents;
    }

    public GeneratedDocument getDocument(String id) {
        DocumentSnapshot snapshot = waitFor(documentsCollection().document(id).get());
        if (!snapshot.exists()) {
            throw new IllegalArgumentException("Document was not found.");
        }

        GeneratedDocument document = snapshot.toObject(GeneratedDocument.class);
        if (document == null) {
            throw new IllegalArgumentException("Document was not found.");
        }
        document.setId(snapshot.getId());
        return document;
    }

    public GeneratedDocument saveDocument(GeneratedDocument document) {
        String id = UUID.randomUUID().toString();
        document.setId(id);
        document.setCreatedDate(Instant.now().toString());
        waitFor(documentsCollection().document(id).set(document));
        return document;
    }

    private DocumentReference profileDocument() {
        return firestore.collection("users")
                .document(USER_ID)
                .collection("resumeProfiles")
                .document("current");
    }

    private CollectionReference documentsCollection() {
        return firestore.collection("users")
                .document(USER_ID)
                .collection("generatedDocuments");
    }

    private <T> T waitFor(ApiFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Firestore request was interrupted.");
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Firestore request failed: " + exception.getCause().getMessage());
        }
    }
}

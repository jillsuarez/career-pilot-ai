package edu.farmingdale.careerpilot.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Bean
    public Firestore firestore(@Value("${firebase.project-id}") String projectId,
                               @Value("${firebase.use-emulator}") boolean useEmulator,
                               @Value("${firebase.emulator-host}") String emulatorHost) throws IOException {
        if (isBlank(projectId)) {
            throw new IllegalStateException("FIREBASE_PROJECT_ID is required.");
        }

        if (useEmulator) {
            if (isBlank(emulatorHost)) {
                throw new IllegalStateException("FIRESTORE_EMULATOR_HOST is required when USE_FIREBASE_EMULATOR is true.");
            }
            return FirestoreOptions.getDefaultInstance().toBuilder()
                    .setProjectId(projectId)
                    .setEmulatorHost(emulatorHost)
                    .setCredentials(NoCredentials.getInstance())
                    .build()
                    .getService();
        }

        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (isBlank(credentialsPath)) {
            throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS must point to your Firebase service-account JSON file.");
        }
        if (!Files.exists(Path.of(credentialsPath))) {
            throw new IllegalStateException("Firebase service-account file was not found: " + credentialsPath);
        }

        if (FirebaseApp.getApps().isEmpty()) {
            try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setProjectId(projectId)
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }

        return FirestoreClient.getFirestore();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

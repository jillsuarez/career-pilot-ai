package edu.farmingdale.careerpilot.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.Test;

class FirebaseConfigTests {

    @Test
    void emulatorClientLetsFirestoreConfigureItsTransportAndCredentials() throws Exception {
        Firestore firestore = new FirebaseConfig().firestore(
                "demo-career-pilot-ai",
                true,
                "127.0.0.1:8081");

        try {
            assertEquals("127.0.0.1:8081", firestore.getOptions().getEmulatorHost());
            assertFalse(firestore.getOptions().getCredentials() instanceof NoCredentials);
        } finally {
            firestore.close();
        }
    }
}

package edu.farmingdale.careerpilot.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "firebase.project-id=demo-career-pilot-ai",
        "firebase.use-emulator=true",
        "firebase.emulator-host=localhost:8081"
})
class CareerPilotBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}

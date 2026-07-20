package edu.farmingdale.careerpilot.frontend;

import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CareerPilotApp extends Application {

    @Override
    public void start(Stage stage) {
        AppCoordinator coordinator = new AppCoordinator();
        Scene scene = new Scene(coordinator.getRoot(), 1120, 760);
        URL stylesheet = getClass().getResource("/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Career Pilot AI");
        stage.setMinWidth(920);
        stage.setMinHeight(640);
        stage.setScene(scene);
        coordinator.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

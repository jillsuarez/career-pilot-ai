package edu.farmingdale.careerpilot.frontend;

import edu.farmingdale.careerpilot.frontend.component.AppShell;
import edu.farmingdale.careerpilot.frontend.navigation.AppNavigator;
import edu.farmingdale.careerpilot.frontend.navigation.Route;
import edu.farmingdale.careerpilot.frontend.service.DemoAuthenticationService;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import edu.farmingdale.careerpilot.frontend.view.LoginView;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class AppCoordinator {

    private final StackPane root = new StackPane();
    private final ApiClient apiClient = new ApiClient();

    public Parent getRoot() {
        return root;
    }

    public void showLogin() {
        LoginView loginView = new LoginView(new DemoAuthenticationService(), this::showApplication);
        root.getChildren().setAll(loginView);
    }

    private void showApplication(String email) {
        AppShell shell = new AppShell();
        shell.setUserEmail(email);
        shell.setOnLogout(this::showLogin);

        UiTaskRunner taskRunner = new UiTaskRunner(shell::setStatus);
        AppNavigator navigator = new AppNavigator(shell, apiClient, taskRunner, email);
        shell.setOnNavigate(navigator::navigate);

        root.getChildren().setAll(shell);
        navigator.navigate(Route.DASHBOARD);
    }
}

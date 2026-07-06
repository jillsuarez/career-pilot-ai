package edu.farmingdale.careerpilot.frontend.navigation;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.component.AppShell;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import edu.farmingdale.careerpilot.frontend.view.DashboardView;
import edu.farmingdale.careerpilot.frontend.view.DocumentsView;
import edu.farmingdale.careerpilot.frontend.view.GenerateView;
import edu.farmingdale.careerpilot.frontend.view.ProfileView;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.Node;

public class AppNavigator {

    private final AppShell shell;
    private final Map<Route, Supplier<Node>> viewFactories = new EnumMap<>(Route.class);

    public AppNavigator(AppShell shell, ApiClient apiClient, UiTaskRunner taskRunner, String userEmail) {
        this.shell = shell;
        viewFactories.put(Route.DASHBOARD, () -> new DashboardView(
                apiClient,
                taskRunner,
                userEmail,
                this::navigate
        ));
        viewFactories.put(Route.PROFILE, () -> new ProfileView(apiClient, taskRunner));
        viewFactories.put(Route.GENERATE, () -> new GenerateView(apiClient, taskRunner));
        viewFactories.put(Route.DOCUMENTS, () -> new DocumentsView(apiClient, taskRunner));
    }

    public void navigate(Route route) {
        Supplier<Node> viewFactory = viewFactories.get(route);
        if (viewFactory == null) {
            throw new IllegalArgumentException("No view registered for route: " + route);
        }
        shell.show(route, viewFactory.get());
    }
}

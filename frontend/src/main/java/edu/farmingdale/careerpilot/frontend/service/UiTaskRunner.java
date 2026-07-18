package edu.farmingdale.careerpilot.frontend.service;

import java.util.function.Consumer;
import javafx.concurrent.Task;

public class UiTaskRunner {

    private final Consumer<String> statusHandler;
    private final Consumer<String> errorHandler;
    private String lastErrorMessage = "";
    private long lastErrorShownAt;

    public UiTaskRunner(Consumer<String> statusHandler) {
        this(statusHandler, message -> { });
    }

    public UiTaskRunner(Consumer<String> statusHandler, Consumer<String> errorHandler) {
        this.statusHandler = statusHandler;
        this.errorHandler = errorHandler == null ? message -> { } : errorHandler;
    }

    public <T> void run(Work<T> work, Consumer<T> onSuccess) {
        run(work, onSuccess, () -> { });
    }

    public <T> void run(Work<T> work, Consumer<T> onSuccess, Runnable onFailure) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.run();
            }
        };
        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            String message = friendlyMessage(error);
            onFailure.run();
            setStatus(message);
            showError(message);
        });

        Thread thread = new Thread(task, "career-pilot-api-task");
        thread.setDaemon(true);
        thread.start();
    }

    public void setStatus(String message) {
        statusHandler.accept(message);
    }

    @FunctionalInterface
    public interface Work<T> {
        T run() throws Exception;
    }

    private void showError(String message) {
        long now = System.currentTimeMillis();
        if (message.equals(lastErrorMessage) && now - lastErrorShownAt < 2_000) {
            return;
        }
        lastErrorMessage = message;
        lastErrorShownAt = now;
        errorHandler.accept(message);
    }

    private String friendlyMessage(Throwable error) {
        if (error == null || error.getMessage() == null || error.getMessage().isBlank()) {
            return "Request failed. Please try again.";
        }
        String message = error.getMessage();
        if (message.contains("Connection refused")) {
            return "Cannot reach the backend. Start the backend server and try again.";
        }
        if (message.contains("connect timed out") || message.contains("HttpConnectTimeoutException")) {
            return "The backend did not respond in time. Please try again.";
        }
        return message;
    }
}

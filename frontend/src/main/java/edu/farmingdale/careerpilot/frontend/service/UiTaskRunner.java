package edu.farmingdale.careerpilot.frontend.service;

import java.util.function.Consumer;
import javafx.concurrent.Task;

public class UiTaskRunner {

    private final Consumer<String> statusHandler;

    public UiTaskRunner(Consumer<String> statusHandler) {
        this.statusHandler = statusHandler;
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
            onFailure.run();
            setStatus(error == null ? "Request failed." : error.getMessage());
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
}

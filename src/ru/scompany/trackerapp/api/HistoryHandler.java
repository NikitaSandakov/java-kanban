package ru.scompany.trackerapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange);
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            } else {
                sendBadRequest(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        List<Task> history = historyManager.getHistory();

        if (history.isEmpty()) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(history);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        try {
            Task task = gson.fromJson(body, Task.class);

            if (task == null) {
                sendBadRequest(exchange);
                return;
            }

            if (taskManager.getTask(task.getId()) != null) {
                sendHasInteractions(exchange);
                return;
            }

            taskManager.createTask(task);
            historyManager.add(task);

            sendCreated(exchange);
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            sendBadRequest(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}

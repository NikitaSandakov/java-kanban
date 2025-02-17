package ru.scompany.trackerapp.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.scompany.trackerapp.exception.NotFoundException;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
        this.gson = GsonConfig.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("GET")) {
                handleGetRequest(exchange);
            } else if (method.equalsIgnoreCase("POST")) {
                handlePostRequest(exchange);
            } else if (method.equalsIgnoreCase("DELETE")) {
                handleDeleteRequest(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException | NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segments.length > 2) {
            int id = Integer.parseInt(segments[2]);
            try {
                Task task = taskManager.getTask(id);
                String responseText = gson.toJson(task);
                historyManager.add(task);
                sendText(exchange, responseText);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getAllTask()));
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        try {
            Task task = gson.fromJson(body, Task.class);
            taskManager.createTask(task);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            System.out.println("JSON Syntax Error: " + e.getMessage());
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            System.out.println("Task creation error: " + e.getMessage());
            sendNotAcceptable(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segments.length > 2) {
            try {
                int id = Integer.parseInt(segments[2]);
                taskManager.removeTaskById(id);
                historyManager.remove(id);
                sendText(exchange, "Task removed successfully");
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

}
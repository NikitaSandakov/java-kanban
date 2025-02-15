package ru.scompany.trackerapp.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.scompany.trackerapp.exception.NotFoundException;
import ru.scompany.trackerapp.model.Subtask;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, HistoryManager historyManager) {
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
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segments.length > 2) {
            try {
                int id = Integer.parseInt(segments[2]);
                Subtask subtask = taskManager.getSubtask(id);
                historyManager.add(subtask);
                String responseText = gson.toJson(subtask);
                sendText(exchange, responseText);
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            List<Subtask> subtasks = taskManager.getAllSubtask();
            sendText(exchange, gson.toJson(subtasks));
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask == null || subtask.getEpicId() == 0) {
                sendBadRequest(exchange);
                return;
            }

            taskManager.createSubtask(subtask);
            sendCreated(exchange);
        } catch (JsonSyntaxException e) {
            System.out.println("JSON Syntax Error: " + e.getMessage());
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            System.out.println("Task creation error: " + e.getMessage());
            sendBadRequest(exchange);
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segments.length > 2) {
            try {
                int id = Integer.parseInt(segments[2]);
                taskManager.removeSubtaskById(id);
                historyManager.remove(id);
                sendText(exchange, "Subtask removed successfully");
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

}
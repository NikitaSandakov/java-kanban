package ru.scompany.trackerapp.api;

import com.sun.net.httpserver.HttpExchange;
import ru.scompany.trackerapp.exception.NotFoundException;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Gson gson;

    public EpicHandler(TaskManager taskManager, HistoryManager historyManager) {
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
                try {
                    Epic epic = taskManager.getEpic(id);
                    String responseText = gson.toJson(epic);
                    historyManager.add(epic);
                    sendText(exchange, responseText);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        } else {
            List<Epic> epics = taskManager.getAllEpic();
            String responseText = gson.toJson(epics);
            sendText(exchange, responseText);
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        Epic epic = gson.fromJson(body, Epic.class);

        if (epic == null) {
            System.out.println("Error: Failed to deserialize the epic object.");
            sendBadRequest(exchange);
            return;
        }

        try {
            taskManager.createEpic(epic);
            sendCreated(exchange);
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating epic: " + e.getMessage());
            sendBadRequest(exchange);
        } catch (Exception e) {
            System.out.println("Unexpected error while creating epic: " + e.getMessage());
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
                taskManager.removeEpicById(id);
                historyManager.remove(id);
                sendText(exchange, "Epic removed successfully");
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

}
package ru.scompany.trackerapp.api;

import com.sun.net.httpserver.HttpExchange;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.service.TaskComparator;
import ru.scompany.trackerapp.service.TaskManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PrioritizedTaskHandler extends BaseHttpHandler {
    private TaskManager taskManager;
    private static final Gson gson = GsonConfig.getGson();

    public PrioritizedTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("GET")) {
                handleGetRequest(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        List<Task> taskList = taskManager.getPrioritizedTasks();
        Set<Task> taskSet = new TreeSet<>(new TaskComparator());
        taskSet.addAll(taskList);
        String responseText = gson.toJson(taskSet);
        sendText(exchange, responseText);
    }

}
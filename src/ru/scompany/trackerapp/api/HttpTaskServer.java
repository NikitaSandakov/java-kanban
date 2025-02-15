package ru.scompany.trackerapp.api;

import com.sun.net.httpserver.HttpServer;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.Managers;
import ru.scompany.trackerapp.service.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.io.File;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static InMemoryTaskManager taskManager = Managers.getFileBackedTaskManager(new File("tasks.csv"));
    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private HttpServer server;

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        Thread.sleep(10 * 60 * 1000);
        httpTaskServer.stop();
    }

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager, historyManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager, historyManager));
        server.createContext("/epics", new EpicHandler(taskManager, historyManager));
        server.createContext("/history", new HistoryHandler(taskManager, historyManager));
        server.createContext("/prioritized", new PrioritizedTaskHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("Сервер слушает порт: " + PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Сервер остановлен");
        }
    }

}
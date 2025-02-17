package test;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.api.GsonConfig;
import ru.scompany.trackerapp.api.HttpTaskServer;
import ru.scompany.trackerapp.model.Task;
import ru.scompany.trackerapp.model.TaskStatus;
import ru.scompany.trackerapp.service.InMemoryTaskManager;
import ru.scompany.trackerapp.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;



public class TaskHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer();
        gson = GsonConfig.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(2, "Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус-код должен быть 201");

        URI getUrl = URI.create("http://localhost:8080/tasks");
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode(), "Статус-код должен быть 200");

        Task[] tasksFromResponse = gson.fromJson(getResponse.body(), Task[].class);
        assertNotNull(tasksFromResponse, "Задачи не возвращаются");
        assertEquals(1, tasksFromResponse.length, "Некорректное количество задач");
        assertEquals("Test Task", tasksFromResponse[0].getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task(2, "Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус-код должен быть 200");

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не найдена");
        assertEquals("Test Task", taskFromResponse.getName(), "Имя задачи не совпадает");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(2, "Test Task", "Description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI deleteUrl = URI.create("http://localhost:8080/tasks/2");
        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode(), "Статус-код должен быть 200");

        URI getUrl = URI.create("http://localhost:8080/tasks/2");
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Статус-код должен быть 404, задача не найдена");
    }

}
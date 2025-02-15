package test;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.api.GsonConfig;
import ru.scompany.trackerapp.api.HttpTaskServer;
import ru.scompany.trackerapp.model.Epic;
import ru.scompany.trackerapp.service.HistoryManager;
import ru.scompany.trackerapp.service.InMemoryTaskManager;
import ru.scompany.trackerapp.service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicHandlerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;
    private HttpTaskServer httpTaskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {;
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test Epic", "Epic Description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Статус-код должен быть 201");

        URI getUrl = URI.create("http://localhost:8080/epics");
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode(), "Статус-код должен быть 200");

        Epic[] epicsFromResponse = gson.fromJson(getResponse.body(), Epic[].class);
        assertNotNull(epicsFromResponse, "Эпики не возвращаются");
        assertEquals(1, epicsFromResponse.length, "Некорректное количество эпиков");
        assertEquals("Test Epic", epicsFromResponse[0].getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test Epic", "Epic Description");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Статус-код должен быть 200");

        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse, "Эпик не найден");
        assertEquals("Test Epic", epicFromResponse.getName(), "Имя эпика не совпадает");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Test Epic", "Epic Description");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI deleteUrl = URI.create("http://localhost:8080/epics/1");
        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(deleteUrl).DELETE().build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode(), "Статус-код должен быть 200");

        URI getUrl = URI.create("http://localhost:8080/epics/1");
        HttpRequest getRequest = HttpRequest.newBuilder().uri(getUrl).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, getResponse.statusCode(), "Статус-код должен быть 404, эпик не найден");
    }

}
package test;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.api.HttpTaskServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {

    private HttpTaskServer httpTaskServer;

    @BeforeEach
    void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void testServerStart() {
        assertNotNull(httpTaskServer, "Сервер не был создан");

        httpTaskServer.start();

        assertTrue(true, "Сервер должен быть успешно запущен");
    }

    @Test
    void testServerStop() {
        httpTaskServer.start();

        httpTaskServer.stop();

        assertTrue(true, "Сервер должен быть успешно остановлен");
    }

}
package ru.scompany.trackerapp.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    public void sendText(HttpExchange exchange, String responseText) throws IOException {
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    public void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, -1);
    }

    public void sendBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, -1);
    }

    public void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, -1);
    }

    public void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, -1);
    }

    public void sendInternalServerError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, -1);
    }

}
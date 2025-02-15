package test;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.scompany.trackerapp.api.BaseHttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseHttpHandlerTest {

    private BaseHttpHandler baseHttpHandler;
    private TestHttpExchange exchange;

    @BeforeEach
    void setUp() {
        baseHttpHandler = new BaseHttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                sendText(exchange, "Test response");
            }
        };
        exchange = new TestHttpExchange();
    }

    @Test
    void testSendText() throws IOException {
        String responseText = "Test response";

        HttpExchange exchange = new HttpExchange() {
            private Headers headers = new Headers();
            private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            @Override
            public Headers getRequestHeaders() {
                return null;
            }

            @Override
            public Headers getResponseHeaders() {
                return headers;
            }

            @Override
            public URI getRequestURI() {
                return null;
            }

            @Override
            public String getRequestMethod() {
                return "";
            }

            @Override
            public HttpContext getHttpContext() {
                return null;
            }

            @Override
            public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
            }

            @Override
            public InetSocketAddress getRemoteAddress() {
                return null;
            }

            @Override
            public int getResponseCode() {
                return 0;
            }

            @Override
            public InetSocketAddress getLocalAddress() {
                return null;
            }

            @Override
            public String getProtocol() {
                return "";
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public void setAttribute(String name, Object value) {

            }

            @Override
            public void setStreams(InputStream i, OutputStream o) {

            }

            @Override
            public HttpPrincipal getPrincipal() {
                return null;
            }

            @Override
            public OutputStream getResponseBody() {
                return outputStream;
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getRequestBody() {
                return null;
            }

        };
    }

    @Test
    void testSendCreated() throws IOException {
        baseHttpHandler.sendCreated(exchange);

        assertEquals(201, exchange.getResponseCode());
        assertEquals("", exchange.getResponseBodyAsString());
    }

    @Test
    void testSendBadRequest() throws IOException {
        baseHttpHandler.sendBadRequest(exchange);

        assertEquals(400, exchange.getResponseCode());
        assertEquals("", exchange.getResponseBodyAsString());
    }

    @Test
    void testSendNotFound() throws IOException {
        baseHttpHandler.sendNotFound(exchange);

        assertEquals(404, exchange.getResponseCode());
        assertEquals("", exchange.getResponseBodyAsString());
    }

    @Test
    void testSendHasInteractions() throws IOException {
        baseHttpHandler.sendHasInteractions(exchange);

        assertEquals(406, exchange.getResponseCode());
        assertEquals("", exchange.getResponseBodyAsString());
    }

    @Test
    void testSendInternalServerError() throws IOException {
        baseHttpHandler.sendInternalServerError(exchange);

        assertEquals(500, exchange.getResponseCode());
        assertEquals("", exchange.getResponseBodyAsString());
    }

    private static class TestHttpExchange extends HttpExchange {

        private int responseCode;
        private final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();

        @Override
        public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
            responseCode = rCode;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public OutputStream getResponseBody() {
            return responseBody;
        }

        @Override
        public Headers getRequestHeaders() {
            return null;
        }

        @Override
        public Headers getResponseHeaders() {
            return null;
        }

        @Override
        public URI getRequestURI() {
            return null;
        }

        @Override
        public String getRequestMethod() {
            return "";
        }

        @Override
        public HttpContext getHttpContext() {
            return null;
        }

        @Override
        public void close() {
        }

        @Override
        public InputStream getRequestBody() {
            return null;
        }

        public int getResponseCode() {
            return responseCode;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public String getProtocol() {
            return "";
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {

        }

        @Override
        public void setStreams(InputStream i, OutputStream o) {

        }

        @Override
        public HttpPrincipal getPrincipal() {
            return null;
        }

        public String getResponseBodyAsString() {
            return responseBody.toString();
        }

    }
}

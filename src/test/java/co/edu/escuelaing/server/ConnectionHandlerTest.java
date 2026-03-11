package co.edu.escuelaing.server;

import co.edu.escuelaing.handlers.RouteRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConnectionHandler")
class ConnectionHandlerTest {

    @BeforeEach
    void setUp() {
        RouteRegistry.clear();
        RouteRegistry.staticfiles("webroot");
    }

    @AfterEach
    void tearDown() {
        RouteRegistry.clear();
    }

    @Nested
    @DisplayName("routing")
    class Routing {

        @Test
        @DisplayName("invokes handler for registered GET route")
        void invokesHandlerForRegisteredRoute() throws Exception {
            RouteRegistry.get("/App/hello", (req, res) ->
                    "Hello " + (req.getValues("name") != null ? req.getValues("name") : "World"));

            String response = sendRequest("GET /App/hello HTTP/1.1\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("200 OK"));
            assertTrue(response.contains("Hello World"));
        }

        @Test
        @DisplayName("passes query params to handler")
        void passesQueryParamsToHandler() throws Exception {
            RouteRegistry.get("/App/hello", (req, res) ->
                    "Hello " + (req.getValues("name") != null ? req.getValues("name") : "World"));

            String response = sendRequest("GET /App/hello?name=Juan HTTP/1.1\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("Hello Juan"));
        }

        @Test
        @DisplayName("returns 404 for unregistered route")
        void returns404ForUnregisteredRoute() throws Exception {
            String response = sendRequest("GET /nonexistent HTTP/1.1\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("404 Not Found"));
        }

        @Test
        @DisplayName("does not match route for POST method")
        void doesNotMatchRouteForPost() throws Exception {
            RouteRegistry.get("/api/get", (req, res) -> "get");

            String response = sendRequest("POST /api/get HTTP/1.1\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("404 Not Found"));
        }
    }

    @Nested
    @DisplayName("static files")
    class StaticFiles {

        @Test
        @DisplayName("serves index.html for root")
        void servesIndexForRoot() throws Exception {
            String response = sendRequest("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("200 OK"));
            assertTrue(response.contains("text/html"));
        }

        @Test
        @DisplayName("serves styles.css")
        void servesStyleCss() throws Exception {
            String response = sendRequest("GET /styles.css HTTP/1.1\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("200 OK"));
            assertTrue(response.contains("text/css"));
        }
    }

    @Nested
    @DisplayName("error handling")
    class ErrorHandling {

        @Test
        @DisplayName("returns 400 for malformed request line")
        void returns400ForMalformedRequest() throws Exception {
            String response = sendRequest("INVALID\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("400 Bad Request"));
        }

        @Test
        @DisplayName("returns 400 when request line has only method")
        void returns400WhenOnlyMethod() throws Exception {
            String response = sendRequest("GET\r\nHost: localhost\r\n\r\n");

            assertTrue(response.contains("400 Bad Request"));
        }

        @Test
        @DisplayName("handles empty request without exception")
        void handlesEmptyRequestGracefully() throws Exception {
            String response = sendRequest("\r\n");
            assertNotNull(response);
        }
    }

    private String sendRequest(String request) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Socket socket = new Socket() {
            @Override
            public InputStream getInputStream() {
                return inputStream;
            }

            @Override
            public OutputStream getOutputStream() {
                return outputStream;
            }
        };

        ConnectionHandler handler = new ConnectionHandler(socket);
        handler.run();

        return outputStream.toString();
    }
}

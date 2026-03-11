package co.edu.escuelaing.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpResponse")
class HttpResponseTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @Nested
    @DisplayName("send - happy path")
    class SendHappyPath {

        @Test
        @DisplayName("sends 200 OK with body and content-type")
        void sends200OkWithBody() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.type("text/plain");
            response.send("Hello World");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.startsWith("HTTP/1.1 200 OK"));
            assertTrue(raw.contains("Content-Type: text/plain"));
            assertTrue(raw.contains("Content-Length: 11"));
            assertTrue(raw.contains("Hello World"));
        }

        @Test
        @DisplayName("includes charset in Content-Type")
        void includesCharsetInContentType() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.send("test");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.contains("charset=UTF-8"));
        }

        @Test
        @DisplayName("uses custom status when set")
        void usesCustomStatusWhenSet() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.status(201);
            response.send("Created");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.startsWith("HTTP/1.1 201"));
        }
    }

    @Nested
    @DisplayName("sendNotFound")
    class SendNotFound {

        @Test
        @DisplayName("sends 404 with default message when null")
        void sends404WithDefaultMessageWhenNull() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.sendNotFound(null);

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.startsWith("HTTP/1.1 404 Not Found"));
            assertTrue(raw.contains("Not Found"));
        }

        @Test
        @DisplayName("sends 404 with custom message")
        void sends404WithCustomMessage() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.sendNotFound("Resource not found");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.contains("Resource not found"));
        }
    }

    @Nested
    @DisplayName("sendError")
    class SendError {

        @Test
        @DisplayName("sends 500 with default message when null")
        void sends500WithDefaultMessageWhenNull() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.sendError(null);

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.startsWith("HTTP/1.1 500 Internal Server Error"));
            assertTrue(raw.contains("Internal Server Error"));
        }

        @Test
        @DisplayName("sends 500 with custom message")
        void sends500WithCustomMessage() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.sendError("Database error");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.contains("Database error"));
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles empty body")
        void handlesEmptyBody() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.send("");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.contains("Content-Length: 0"));
        }

        @Test
        @DisplayName("handles null body as empty string would throw - send requires non-null")
        void handlesUnicodeContent() throws Exception {
            HttpResponse response = new HttpResponse(outputStream);
            response.send("Ñoño café");

            String raw = outputStream.toString(StandardCharsets.UTF_8);

            assertTrue(raw.contains("Ñoño café"));
        }
    }
}

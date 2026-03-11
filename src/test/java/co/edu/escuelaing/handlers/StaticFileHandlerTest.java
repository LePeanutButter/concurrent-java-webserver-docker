package co.edu.escuelaing.handlers;

import co.edu.escuelaing.http.HttpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StaticFileHandler")
class StaticFileHandlerTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        RouteRegistry.clear();
        RouteRegistry.staticfiles("webroot");
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        RouteRegistry.clear();
    }

    @Nested
    @DisplayName("handle - happy path")
    class HandleHappyPath {

        @Test
        @DisplayName("serves index.html for root path")
        void servesIndexForRootPath() {
            HttpRequest request = new HttpRequest("GET", "/", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertTrue(handled);
            String response = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(response.startsWith("HTTP/1.1 200 OK"));
            assertTrue(response.contains("text/html"));
        }

        @Test
        @DisplayName("serves styles.css with correct Content-Type")
        void servesStyleCssWithCorrectContentType() {
            HttpRequest request = new HttpRequest("GET", "/styles.css", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertTrue(handled);
            String response = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(response.contains("Content-Type: text/css"));
        }

        @Test
        @DisplayName("serves app.js with correct Content-Type")
        void servesAppJsWithCorrectContentType() {
            HttpRequest request = new HttpRequest("GET", "/app.js", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertTrue(handled);
            String response = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(response.contains("Content-Type: application/javascript"));
        }

        @Test
        @DisplayName("serves SVG image with correct Content-Type")
        void servesSvgWithCorrectContentType() {
            HttpRequest request = new HttpRequest("GET", "/images/logo.svg", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertTrue(handled);
            String response = outputStream.toString(StandardCharsets.UTF_8);
            assertTrue(response.contains("Content-Type: image/svg+xml"));
        }
    }

    @Nested
    @DisplayName("handle - edge cases")
    class HandleEdgeCases {

        @Test
        @DisplayName("returns false for nonexistent file")
        void returnsFalseForNonexistentFile() {
            HttpRequest request = new HttpRequest("GET", "/nonexistent.html", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertFalse(handled);
        }

        @Test
        @DisplayName("returns false for path traversal attempt")
        void returnsFalseForPathTraversal() {
            HttpRequest request = new HttpRequest("GET", "/../etc/passwd", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertFalse(handled);
        }

        @Test
        @DisplayName("returns false for path with encoded traversal")
        void returnsFalseForEncodedPathTraversal() {
            HttpRequest request = new HttpRequest("GET", "/..%2F..%2Fetc%2Fpasswd", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertFalse(handled);
        }

        @Test
        @DisplayName("handles null path by serving index")
        void handlesNullPath() {
            HttpRequest request = new HttpRequest("GET", null, "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertTrue(handled);
        }
    }

    @Nested
    @DisplayName("handle - custom static folder")
    class HandleCustomStaticFolder {

        @Test
        @DisplayName("uses configured static folder")
        void usesConfiguredStaticFolder() {
            RouteRegistry.staticfiles("webroot");
            HttpRequest request = new HttpRequest("GET", "/index.html", "HTTP/1.1", java.util.Map.of(), java.util.Map.of());

            boolean handled = StaticFileHandler.handle(request, outputStream);

            assertTrue(handled);
        }
    }
}

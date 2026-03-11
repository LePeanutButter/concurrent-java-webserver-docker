package co.edu.escuelaing.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpRequest")
class HttpRequestTest {

    @Nested
    @DisplayName("parse - happy path")
    class ParseHappyPath {

        @Test
        @DisplayName("parses GET request without query params")
        void parsesGetRequestWithoutQueryParams() throws Exception {
            String requestLine = "GET /App/hello HTTP/1.1";
            BufferedReader reader = new BufferedReader(new StringReader("Host: localhost\r\n\r\n"));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertEquals("GET", request.method());
            assertEquals("/App/hello", request.path());
            assertEquals("HTTP/1.1", request.version());
            assertTrue(request.queryParams().isEmpty());
        }

        @Test
        @DisplayName("parses GET request with single query param")
        void parsesGetRequestWithSingleQueryParam() throws Exception {
            String requestLine = "GET /App/hello?name=Juan HTTP/1.1";
            BufferedReader reader = new BufferedReader(new StringReader("\r\n"));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertEquals("GET", request.method());
            assertEquals("/App/hello", request.path());
            assertEquals("Juan", request.queryParams().get("name"));
        }

        @Test
        @DisplayName("parses GET request with multiple query params")
        void parsesGetRequestWithMultipleQueryParams() throws Exception {
            String requestLine = "GET /search?q=test&page=2&sort=asc HTTP/1.1";
            BufferedReader reader = new BufferedReader(new StringReader("\r\n"));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertEquals("test", request.queryParams().get("q"));
            assertEquals("2", request.queryParams().get("page"));
            assertEquals("asc", request.queryParams().get("sort"));
        }

        @Test
        @DisplayName("parses headers correctly")
        void parsesHeadersCorrectly() throws Exception {
            String requestLine = "GET / HTTP/1.1";
            String headers = "Host: localhost:8080\r\nAccept: text/html\r\n\r\n";
            BufferedReader reader = new BufferedReader(new StringReader(headers));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertEquals("localhost:8080", request.headers().get("Host"));
            assertEquals("text/html", request.headers().get("Accept"));
        }

        @Test
        @DisplayName("uses HTTP/1.1 when version omitted")
        void usesHttp11WhenVersionOmitted() throws Exception {
            String requestLine = "GET /path";
            BufferedReader reader = new BufferedReader(new StringReader("\r\n"));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertEquals("HTTP/1.1", request.version());
        }
    }

    @Nested
    @DisplayName("parse - edge cases")
    class ParseEdgeCases {

        @Test
        @DisplayName("throws MalformedRequestException when requestLine is null")
        void throwsWhenRequestLineIsNull() throws IOException {
            BufferedReader reader = new BufferedReader(new StringReader(""));

            assertThrows(MalformedRequestException.class, () ->
                    HttpRequest.parse(null, reader));
        }

        @Test
        @DisplayName("throws MalformedRequestException when requestLine is blank")
        void throwsWhenRequestLineIsBlank() throws IOException {
            BufferedReader reader = new BufferedReader(new StringReader(""));

            assertThrows(MalformedRequestException.class, () ->
                    HttpRequest.parse("   ", reader));
        }

        @Test
        @DisplayName("throws MalformedRequestException when only method present")
        void throwsWhenOnlyMethodPresent() throws IOException {
            BufferedReader reader = new BufferedReader(new StringReader(""));

            assertThrows(MalformedRequestException.class, () ->
                    HttpRequest.parse("GET", reader));
        }

        @Test
        @DisplayName("handles query param without value")
        void handlesQueryParamWithoutValue() throws Exception {
            String requestLine = "GET /path?keyOnly HTTP/1.1";
            BufferedReader reader = new BufferedReader(new StringReader("\r\n"));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertFalse(request.queryParams().containsKey("keyOnly"));
        }

        @Test
        @DisplayName("stores raw query values including URL-encoded")
        void storesRawQueryValues() throws Exception {
            String requestLine = "GET /path?name=Juan%20Perez HTTP/1.1";
            BufferedReader reader = new BufferedReader(new StringReader("\r\n"));

            HttpRequest request = HttpRequest.parse(requestLine, reader);

            assertEquals("Juan%20Perez", request.queryParams().get("name"));
        }
    }

    @Nested
    @DisplayName("getValues and getValue")
    class QueryParamAccess {

        @Test
        @DisplayName("getValues returns value when key exists")
        void getValuesReturnsValueWhenKeyExists() throws Exception {
            HttpRequest request = createRequestWithParams(Map.of("name", "World"));

            assertEquals("World", request.getValues("name"));
        }

        @Test
        @DisplayName("getValues returns null when key does not exist")
        void getValuesReturnsNullWhenKeyMissing() throws Exception {
            HttpRequest request = createRequestWithParams(Map.of());

            assertNull(request.getValues("missing"));
        }

        @Test
        @DisplayName("getValue returns Optional with value when key exists")
        void getValueReturnsOptionalWhenKeyExists() throws Exception {
            HttpRequest request = createRequestWithParams(Map.of("key", "value"));

            assertTrue(request.getValue("key").isPresent());
            assertEquals("value", request.getValue("key").orElseThrow());
        }

        @Test
        @DisplayName("getValue returns empty Optional when key does not exist")
        void getValueReturnsEmptyOptionalWhenKeyMissing() throws Exception {
            HttpRequest request = createRequestWithParams(Map.of());

            assertTrue(request.getValue("missing").isEmpty());
        }
    }

    private HttpRequest createRequestWithParams(Map<String, String> queryParams) {
        return new HttpRequest("GET", "/path", "HTTP/1.1", Map.of(), queryParams);
    }
}

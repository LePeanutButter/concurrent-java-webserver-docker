package co.edu.escuelaing.handlers;

import co.edu.escuelaing.http.HttpRequest;
import co.edu.escuelaing.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RouteRegistry")
class RouteRegistryTest {

    @BeforeEach
    void setUp() {
        RouteRegistry.clear();
    }

    @AfterEach
    void tearDown() {
        RouteRegistry.clear();
    }

    @Nested
    @DisplayName("get")
    class GetMethod {

        @Test
        @DisplayName("registers route and handler")
        void registersRouteAndHandler() {
            RouteHandler handler = (req, res) -> "ok";

            RouteRegistry.get("/test", handler);

            assertEquals(handler, RouteRegistry.findGetRoute("/test"));
        }

        @Test
        @DisplayName("throws when path is null")
        void throwsWhenPathIsNull() {
            RouteHandler handler = (req, res) -> "ok";

            assertThrows(IllegalArgumentException.class, () ->
                    RouteRegistry.get(null, handler));
        }

        @Test
        @DisplayName("throws when handler is null")
        void throwsWhenHandlerIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    RouteRegistry.get("/test", null));
        }

        @Test
        @DisplayName("overwrites existing route for same path")
        void overwritesExistingRoute() {
            RouteHandler handler1 = (req, res) -> "first";
            RouteHandler handler2 = (req, res) -> "second";

            RouteRegistry.get("/same", handler1);
            RouteRegistry.get("/same", handler2);

            assertEquals(handler2, RouteRegistry.findGetRoute("/same"));
        }
    }

    @Nested
    @DisplayName("findGetRoute")
    class FindGetRoute {

        @Test
        @DisplayName("returns null for unregistered path")
        void returnsNullForUnregisteredPath() {
            assertNull(RouteRegistry.findGetRoute("/nonexistent"));
        }

        @Test
        @DisplayName("returns handler for registered path")
        void returnsHandlerForRegisteredPath() {
            RouteHandler handler = (req, res) -> "hello";
            RouteRegistry.get("/App/hello", handler);

            assertEquals(handler, RouteRegistry.findGetRoute("/App/hello"));
        }
    }

    @Nested
    @DisplayName("staticfiles")
    class Staticfiles {

        @Test
        @DisplayName("configures static folder")
        void configuresStaticFolder() {
            RouteRegistry.staticfiles("custom");

            assertEquals("custom", RouteRegistry.getStaticFolder());
        }

        @Test
        @DisplayName("defaults to webroot when null")
        void defaultsToWebrootWhenNull() {
            RouteRegistry.staticfiles("other");
            RouteRegistry.staticfiles(null);

            assertEquals("webroot", RouteRegistry.getStaticFolder());
        }
    }

    @Nested
    @DisplayName("getRoutesView")
    class GetRoutesView {

        @Test
        @DisplayName("returns unmodifiable map")
        void returnsUnmodifiableMap() {
            RouteRegistry.get("/test", (req, res) -> "ok");
            Map<String, RouteHandler> view = RouteRegistry.getRoutesView();

            assertThrows(UnsupportedOperationException.class, () ->
                    view.put("/other", (req, res) -> "no"));
        }
    }
}

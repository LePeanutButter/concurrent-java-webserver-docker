package co.edu.escuelaing.server;

import co.edu.escuelaing.config.ServerConfig;
import co.edu.escuelaing.handlers.RouteRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpServer - Integration")
class HttpServerIntegrationTest {

    private HttpServer server;
    private Thread serverThread;
    private int port;

    @BeforeEach
    void setUp() {
        RouteRegistry.clear();
        RouteRegistry.staticfiles("webroot");
        RouteRegistry.get("/App/pi", (req, res) -> String.valueOf(Math.PI));
        port = findAvailablePort();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        if (server != null) {
            server.stop();
        }
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            serverThread.join(2000);
        }
        RouteRegistry.clear();
    }

    @Test
    @DisplayName("server accepts connection and responds to GET")
    void serverRespondsToGetRequest() throws Exception {
        ServerConfig config = new ServerConfig(port, java.nio.charset.StandardCharsets.UTF_8, "webroot");
        server = new HttpServer(config);

        CountDownLatch started = new CountDownLatch(1);
        serverThread = new Thread(() -> {
            started.countDown();
            server.start();
        });
        serverThread.start();
        assertTrue(started.await(2, TimeUnit.SECONDS));

        Thread.sleep(500);

        try (Socket socket = new Socket("127.0.0.1", port)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String request = "GET /App/pi HTTP/1.1\r\nHost: localhost\r\n\r\n";
            out.write(request.getBytes());
            out.flush();
            socket.shutdownOutput();

            String response = new String(in.readAllBytes());
            assertTrue(response.contains("200 OK"));
            assertTrue(response.contains("3.14"));
        }
    }

    @Test
    @DisplayName("server serves static file")
    void serverServesStaticFile() throws Exception {
        ServerConfig config = new ServerConfig(port, java.nio.charset.StandardCharsets.UTF_8, "webroot");
        server = new HttpServer(config);

        CountDownLatch started = new CountDownLatch(1);
        serverThread = new Thread(() -> {
            started.countDown();
            server.start();
        });
        serverThread.start();
        assertTrue(started.await(2, TimeUnit.SECONDS));

        Thread.sleep(500);

        try (Socket socket = new Socket("127.0.0.1", port)) {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            String request = "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
            out.write(request.getBytes());
            out.flush();
            socket.shutdownOutput();

            String response = new String(in.readAllBytes());
            assertTrue(response.contains("200 OK"));
            assertTrue(response.contains("text/html"));
        }
    }

    private int findAvailablePort() {
        try (java.net.ServerSocket s = new java.net.ServerSocket(0)) {
            return s.getLocalPort();
        } catch (IOException e) {
            return 18080 + (int) (Math.random() * 1000);
        }
    }
}

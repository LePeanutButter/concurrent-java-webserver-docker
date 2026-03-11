package co.edu.escuelaing;

import co.edu.escuelaing.config.ServerConfig;
import co.edu.escuelaing.handlers.RouteRegistry;
import co.edu.escuelaing.ioc.container.ApplicationContext;
import co.edu.escuelaing.server.HttpServer;

/**
 * Punto de entrada de la aplicación.
 * Configura rutas, archivos estáticos e inicia el servidor HTTP.
 */
public class Main {

    private static final String BASE_PACKAGE = "co.edu.escuelaing";
    private static final String STATIC_FOLDER = "webroot";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        configureRoutes();
        startServer();
    }

    private static void configureRoutes() {
        RouteRegistry.staticfiles(STATIC_FOLDER);

        ApplicationContext context = new ApplicationContext(BASE_PACKAGE);
        context.loadControllers();
    }

    private static void startServer() {
        ServerConfig config = ServerConfig.withPort(SERVER_PORT);
        HttpServer server = new HttpServer(config);

        Thread serverThread = new Thread(server::start, "http-server-main");
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            try {
                serverThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "http-server-shutdown-hook"));

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            server.stop();
        }
    }
}

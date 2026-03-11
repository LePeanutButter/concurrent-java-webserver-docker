package co.edu.escuelaing.server;

import co.edu.escuelaing.config.ServerConfig;
import co.edu.escuelaing.utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Servidor HTTP basado en TCP que acepta conexiones y las delega a ConnectionHandler.
 * Utiliza virtual threads (Java 21) para concurrencia.
 */
public class HttpServer {

    private final ServerConfig config;
    private final ExecutorService executor;
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public HttpServer(ServerConfig config) {
        this.config = config != null ? config : ServerConfig.createDefault();
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public HttpServer(int port) {
        this(ServerConfig.withPort(port));
    }

    public void start() {
        if (running) {
            Logger.info("Server is already running");
            return;
        }

        try {
            serverSocket = new ServerSocket(config.getPort());
            running = true;
            Logger.info("HTTP Server started on port " + config.getPort());

            while (running) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                } catch (SocketException e) {
                    if (!running) {
                        Logger.info("Server socket closed, stopping accept loop");
                        break;
                    }
                    Logger.error("Socket error while accepting connections: " + e.getMessage());
                    continue;
                }
                executor.submit(new ConnectionHandler(clientSocket));
            }
        } catch (IOException e) {
            Logger.error("Failed to start server: " + e.getMessage());
        } finally {
            shutdownExecutor();
            closeServerSocket();
        }
    }

    public void stop() {
        running = false;
        closeServerSocket();
        Logger.info("HTTP Server stop requested");
    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Logger.error("Error closing server socket: " + e.getMessage());
            }
        }
    }

    private void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                Logger.info("Forcing executor shutdown, some connections may still be active");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }
}

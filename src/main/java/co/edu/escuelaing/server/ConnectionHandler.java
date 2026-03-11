package co.edu.escuelaing.server;

import co.edu.escuelaing.handlers.RouteHandler;
import co.edu.escuelaing.handlers.RouteRegistry;
import co.edu.escuelaing.handlers.StaticFileHandler;
import co.edu.escuelaing.http.HttpRequest;
import co.edu.escuelaing.http.HttpResponse;
import co.edu.escuelaing.http.MalformedRequestException;
import co.edu.escuelaing.utils.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Procesa una conexión HTTP individual.
 * Delega el manejo a rutas registradas o al handler de archivos estáticos.
 */
public class ConnectionHandler implements Runnable {

    private static final String METHOD_GET = "GET";

    private final Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            HttpRequest httpRequest;
            try {
                httpRequest = HttpRequest.parse(requestLine, reader);
            } catch (MalformedRequestException e) {
                sendBadRequest(out);
                return;
            }
            RouteHandler handler = RouteRegistry.findGetRoute(httpRequest.path());

            if (METHOD_GET.equals(httpRequest.method()) && handler != null) {
                handleRoute(httpRequest, handler, out);
                return;
            }

            if (StaticFileHandler.handle(httpRequest, out)) {
                return;
            }

            sendNotFound(out);

        } catch (Exception e) {
            String message = e.getMessage();
            if (socket.isClosed() || (message != null && message.contains("Closed by interrupt"))) {
                Logger.info("Connection closed during shutdown");
            } else {
                Logger.error("Connection error: " + message);
            }
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                Logger.error("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleRoute(HttpRequest request, RouteHandler handler, OutputStream out) throws Exception {
        HttpResponse response = new HttpResponse(out);
        String result = handler.handle(request, response);
        response.type("text/plain");
        response.send(result != null ? result : "");
    }

    private void sendNotFound(OutputStream out) {
        sendErrorResponse(out, 404, "Not Found");
    }

    private void sendBadRequest(OutputStream out) {
        sendErrorResponse(out, 400, "Bad Request");
    }

    private void sendErrorResponse(OutputStream out, int status, String message) {
        try {
            String body = "HTTP/1.1 " + status + " " + message + "\r\nContent-Type: text/plain; charset=UTF-8\r\n\r\n" + message;
            out.write(body.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            out.flush();
        } catch (Exception e) {
            Logger.error("Error sending " + status + ": " + e.getMessage());
        }
    }
}

package co.edu.escuelaing.http;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Construye y envía una respuesta HTTP.
 */
public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final OutputStream outputStream;
    private final Charset charset;
    private int status = HttpStatus.OK;
    private String contentType = "text/plain";

    public HttpResponse(OutputStream outputStream) {
        this(outputStream, DEFAULT_CHARSET);
    }

    public HttpResponse(OutputStream outputStream, Charset charset) {
        this.outputStream = outputStream;
        this.charset = charset != null ? charset : DEFAULT_CHARSET;
    }

    public void status(int status) {
        this.status = status;
    }

    public void type(String type) {
        this.contentType = type;
    }

    /**
     * Envía el cuerpo de la respuesta con el status configurado.
     */
    public void send(String body) throws Exception {
        byte[] bodyBytes = body.getBytes(charset);

        PrintWriter writer = new PrintWriter(outputStream, false, charset);
        writer.print("HTTP/1.1 " + status + " " + HttpStatus.reasonPhrase(status) + CRLF);
        writer.print("Content-Type: " + contentType + "; charset=" + charset.name() + CRLF);
        writer.print("Content-Length: " + bodyBytes.length + CRLF);
        writer.print(CRLF);
        writer.flush();

        outputStream.write(bodyBytes);
        outputStream.flush();
    }

    public void sendNotFound(String message) throws Exception {
        status(HttpStatus.NOT_FOUND);
        type("text/plain");
        send(message != null ? message : "Not Found");
    }

    public void sendError(String message) throws Exception {
        status(HttpStatus.INTERNAL_SERVER_ERROR);
        type("text/plain");
        send(message != null ? message : "Internal Server Error");
    }
}

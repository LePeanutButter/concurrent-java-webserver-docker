package co.edu.escuelaing.handlers;

import co.edu.escuelaing.http.HttpRequest;
import co.edu.escuelaing.utils.MimeTypeMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Sirve archivos estáticos desde el classpath (resources).
 * Previene path traversal validando que el path no contenga "..".
 */
public class StaticFileHandler {

    private static final String PATH_TRAVERSAL = "..";
    private static final String INDEX_FILE = "/index.html";

    public static boolean handle(HttpRequest request, OutputStream out) {
        try {
            String requestPath = normalizePath(request.path());
            if (containsPathTraversal(requestPath)) {
                return false;
            }

            String baseFolder = RouteRegistry.getStaticFolder();
            String resourcePath = baseFolder + requestPath;

            InputStream inputStream = StaticFileHandler.class
                    .getClassLoader()
                    .getResourceAsStream(resourcePath);

            if (inputStream == null) {
                return false;
            }

            byte[] content = inputStream.readAllBytes();
            String mimeType = MimeTypeMapper.getMimeType(extractFileName(requestPath));

            writeResponse(out, content, mimeType);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return INDEX_FILE;
        }
        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
        if (path.equals("/")) {
            return INDEX_FILE;
        }
        return path;
    }

    private static boolean containsPathTraversal(String path) {
        return path != null && path.contains(PATH_TRAVERSAL);
    }

    private static String extractFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    private static void writeResponse(OutputStream out, byte[] content, String mimeType) throws IOException {
        out.write(("HTTP/1.1 200 OK\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Type: " + mimeType + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Length: " + content.length + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
        out.write(content);
        out.flush();
    }
}

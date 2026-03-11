package co.edu.escuelaing.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Representa una solicitud HTTP parseada.
 */
public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> headers,
        Map<String, String> queryParams) {

    private static final String QUERY_SEPARATOR = "?";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public static HttpRequest parse(String requestLine, BufferedReader reader) throws IOException, MalformedRequestException {
        if (requestLine == null || requestLine.isBlank()) {
            throw new MalformedRequestException("Request line is empty");
        }
        String[] parts = requestLine.trim().split("\\s+");
        if (parts.length < 2) {
            throw new MalformedRequestException("Request line must contain at least method and path: " + requestLine);
        }
        String method = parts[0];
        String fullPath = parts[1];
        String version = parts.length > 2 ? parts[2] : "HTTP/1.1";

        String path = fullPath;
        Map<String, String> queryParams = new HashMap<>();

        if (fullPath.contains(QUERY_SEPARATOR)) {
            int queryIndex = fullPath.indexOf(QUERY_SEPARATOR);
            path = fullPath.substring(0, queryIndex);
            String queryString = fullPath.substring(queryIndex + 1);

            for (String param : queryString.split(PARAM_SEPARATOR)) {
                String[] keyValue = param.split(KEY_VALUE_SEPARATOR, 2);
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            int sep = line.indexOf(":");
            if (sep > 0) {
                headers.put(line.substring(0, sep).trim(), line.substring(sep + 1).trim());
            }
        }

        return new HttpRequest(method, path, version, headers, queryParams);
    }

    /**
     * Obtiene un parámetro de query por nombre (API principal).
     *
     * @param key nombre del parámetro
     * @return el valor o null si no existe
     */
    public String getValues(String key) {
        return queryParams.get(key);
    }

    /**
     * Obtiene un parámetro de query como Optional.
     *
     * @param key nombre del parámetro
     * @return Optional con el valor o vacío si no existe
     */
    public Optional<String> getValue(String key) {
        return Optional.ofNullable(queryParams.get(key));
    }
}

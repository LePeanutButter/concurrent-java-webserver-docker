package co.edu.escuelaing.handlers;

import co.edu.escuelaing.http.HttpRequest;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro de rutas GET del servidor.
 * Thread-safe para soportar concurrencia con virtual threads.
 */
public class RouteRegistry {

    private static final Map<String, RouteHandler> getRoutes = new ConcurrentHashMap<>();
    private static volatile String staticFolder = "webroot";

    public static void get(String path, RouteHandler handler) {
        if (path == null || handler == null) {
            throw new IllegalArgumentException("path and handler must not be null");
        }
        getRoutes.put(path, handler);
    }

    public static RouteHandler findGetRoute(String path) {
        return getRoutes.get(path);
    }

    public static void staticfiles(String folder) {
        staticFolder = folder != null ? folder : "webroot";
    }

    public static String getStaticFolder() {
        return staticFolder;
    }

    public static Map<String, RouteHandler> getRoutesView() {
        return Collections.unmodifiableMap(getRoutes);
    }

    /**
     * Limpia el registro de rutas y restablece la carpeta estática por defecto.
     * Usar solo en tests para aislamiento.
     */
    public static void clear() {
        getRoutes.clear();
        staticFolder = "webroot";
    }
}

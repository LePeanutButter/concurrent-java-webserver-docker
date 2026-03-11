package co.edu.escuelaing.handlers;

import co.edu.escuelaing.http.HttpRequest;
import co.edu.escuelaing.http.HttpResponse;

/**
 * Interface funcional para manejar solicitudes HTTP en rutas registradas.
 */
@FunctionalInterface
public interface RouteHandler {
    String handle(HttpRequest req, HttpResponse res) throws Exception;
}

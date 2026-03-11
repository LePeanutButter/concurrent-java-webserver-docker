package co.edu.escuelaing.http;

/**
 * Excepción lanzada cuando el request HTTP está mal formado.
 */
public class MalformedRequestException extends Exception {

    public MalformedRequestException(String message) {
        super(message);
    }

    public MalformedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

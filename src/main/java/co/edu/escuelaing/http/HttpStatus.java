package co.edu.escuelaing.http;

/**
 * Constantes de códigos de estado HTTP.
 */
public final class HttpStatus {

    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

    private HttpStatus() {
    }

    public static String reasonPhrase(int status) {
        return switch (status) {
            case OK -> "OK";
            case BAD_REQUEST -> "Bad Request";
            case NOT_FOUND -> "Not Found";
            case INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}

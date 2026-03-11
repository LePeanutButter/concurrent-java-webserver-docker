package co.edu.escuelaing.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Configuración del servidor HTTP.
 * Centraliza puerto, charset y rutas de recursos estáticos.
 */
public class ServerConfig {

    public static final int DEFAULT_PORT = 8080;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_STATIC_FOLDER = "webroot";

    private final int port;
    private final Charset charset;
    private final String staticFolder;

    public ServerConfig() {
        this(DEFAULT_PORT, DEFAULT_CHARSET, DEFAULT_STATIC_FOLDER);
    }

    public ServerConfig(int port, Charset charset, String staticFolder) {
        this.port = port;
        this.charset = charset;
        this.staticFolder = staticFolder != null ? staticFolder : DEFAULT_STATIC_FOLDER;
    }

    public static ServerConfig createDefault() {
        return new ServerConfig();
    }

    public static ServerConfig withPort(int port) {
        return new ServerConfig(port, DEFAULT_CHARSET, DEFAULT_STATIC_FOLDER);
    }

    public int getPort() {
        return port;
    }

    public Charset getCharset() {
        return charset;
    }

    public String getStaticFolder() {
        return staticFolder;
    }
}

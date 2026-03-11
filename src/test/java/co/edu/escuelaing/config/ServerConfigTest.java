package co.edu.escuelaing.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ServerConfig")
class ServerConfigTest {

    @Nested
    @DisplayName("constructors")
    class Constructors {

        @Test
        @DisplayName("default constructor uses default values")
        void defaultConstructorUsesDefaults() {
            ServerConfig config = new ServerConfig();

            assertEquals(ServerConfig.DEFAULT_PORT, config.getPort());
            assertEquals(ServerConfig.DEFAULT_CHARSET, config.getCharset());
            assertEquals(ServerConfig.DEFAULT_STATIC_FOLDER, config.getStaticFolder());
        }

        @Test
        @DisplayName("full constructor sets all values")
        void fullConstructorSetsAllValues() {
            ServerConfig config = new ServerConfig(9090, StandardCharsets.ISO_8859_1, "static");

            assertEquals(9090, config.getPort());
            assertEquals(StandardCharsets.ISO_8859_1, config.getCharset());
            assertEquals("static", config.getStaticFolder());
        }

        @Test
        @DisplayName("full constructor defaults staticFolder when null")
        void fullConstructorDefaultsStaticFolderWhenNull() {
            ServerConfig config = new ServerConfig(8080, StandardCharsets.UTF_8, null);

            assertEquals(ServerConfig.DEFAULT_STATIC_FOLDER, config.getStaticFolder());
        }
    }

    @Nested
    @DisplayName("factory methods")
    class FactoryMethods {

        @Test
        @DisplayName("createDefault returns config with default values")
        void createDefaultReturnsDefaultConfig() {
            ServerConfig config = ServerConfig.createDefault();

            assertEquals(ServerConfig.DEFAULT_PORT, config.getPort());
        }

        @Test
        @DisplayName("withPort sets port and keeps other defaults")
        void withPortSetsPortAndKeepsDefaults() {
            ServerConfig config = ServerConfig.withPort(3000);

            assertEquals(3000, config.getPort());
            assertEquals(ServerConfig.DEFAULT_CHARSET, config.getCharset());
            assertEquals(ServerConfig.DEFAULT_STATIC_FOLDER, config.getStaticFolder());
        }
    }
}

package co.edu.escuelaing.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MalformedRequestException")
class MalformedRequestExceptionTest {

    @Test
    @DisplayName("constructs with message")
    void constructsWithMessage() {
        MalformedRequestException e = new MalformedRequestException("Invalid request");

        assertEquals("Invalid request", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    @DisplayName("constructs with message and cause")
    void constructsWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        MalformedRequestException e = new MalformedRequestException("Wrapped", cause);

        assertEquals("Wrapped", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    @Test
    @DisplayName("is instance of Exception")
    void isInstanceOfException() {
        MalformedRequestException e = new MalformedRequestException("test");

        assertInstanceOf(Exception.class, e);
    }
}

package co.edu.escuelaing.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpStatus")
class HttpStatusTest {

    @Test
    @DisplayName("reasonPhrase returns correct phrases for known status codes")
    void reasonPhraseReturnsCorrectPhrases() {
        assertEquals("OK", HttpStatus.reasonPhrase(HttpStatus.OK));
        assertEquals("Bad Request", HttpStatus.reasonPhrase(HttpStatus.BAD_REQUEST));
        assertEquals("Not Found", HttpStatus.reasonPhrase(HttpStatus.NOT_FOUND));
        assertEquals("Internal Server Error", HttpStatus.reasonPhrase(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("reasonPhrase returns Unknown for unknown status code")
    void reasonPhraseReturnsUnknownForUnknownCode() {
        assertEquals("Unknown", HttpStatus.reasonPhrase(999));
        assertEquals("Unknown", HttpStatus.reasonPhrase(418));
    }
}

package co.edu.escuelaing.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MimeTypeMapper")
class MimeTypeMapperTest {

    @Nested
    @DisplayName("getMimeType - known extensions")
    class KnownExtensions {

        @Test
        @DisplayName("returns text/html for html")
        void returnsTextHtmlForHtml() {
            assertEquals("text/html", MimeTypeMapper.getMimeType("index.html"));
        }

        @Test
        @DisplayName("returns text/css for css")
        void returnsTextCssForCss() {
            assertEquals("text/css", MimeTypeMapper.getMimeType("styles.css"));
        }

        @Test
        @DisplayName("returns application/javascript for js")
        void returnsApplicationJsForJs() {
            assertEquals("application/javascript", MimeTypeMapper.getMimeType("app.js"));
        }

        @Test
        @DisplayName("returns image/png for png")
        void returnsImagePngForPng() {
            assertEquals("image/png", MimeTypeMapper.getMimeType("logo.png"));
        }

        @Test
        @DisplayName("returns image/jpeg for jpg and jpeg")
        void returnsImageJpegForJpgAndJpeg() {
            assertEquals("image/jpeg", MimeTypeMapper.getMimeType("photo.jpg"));
            assertEquals("image/jpeg", MimeTypeMapper.getMimeType("photo.jpeg"));
        }

        @Test
        @DisplayName("returns image types for gif, svg, ico, webp")
        void returnsCorrectTypesForImageFormats() {
            assertEquals("image/gif", MimeTypeMapper.getMimeType("anim.gif"));
            assertEquals("image/svg+xml", MimeTypeMapper.getMimeType("icon.svg"));
            assertEquals("image/x-icon", MimeTypeMapper.getMimeType("favicon.ico"));
            assertEquals("image/webp", MimeTypeMapper.getMimeType("img.webp"));
        }
    }

    @Nested
    @DisplayName("getMimeType - edge cases")
    class EdgeCases {

        @Test
        @DisplayName("returns text/plain for unknown extension")
        void returnsTextPlainForUnknownExtension() {
            assertEquals("text/plain", MimeTypeMapper.getMimeType("file.xyz"));
        }

        @Test
        @DisplayName("returns text/plain when no extension")
        void returnsTextPlainWhenNoExtension() {
            assertEquals("text/plain", MimeTypeMapper.getMimeType("README"));
        }

        @Test
        @DisplayName("handles filename with multiple dots")
        void handlesFilenameWithMultipleDots() {
            assertEquals("text/html", MimeTypeMapper.getMimeType("page.v2.html"));
        }
    }
}

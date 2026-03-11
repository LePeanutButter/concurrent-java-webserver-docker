package co.edu.escuelaing.utils;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeMapper {

    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("ico", "image/x-icon");
        mimeTypes.put("webp", "image/webp");
    }

    public static String getMimeType(String filename) {
        int dot = filename.lastIndexOf(".");
        if (dot == -1) return "text/plain";
        String ext = filename.substring(dot + 1);
        return mimeTypes.getOrDefault(ext, "text/plain");
    }
}
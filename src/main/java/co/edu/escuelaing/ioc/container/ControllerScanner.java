package co.edu.escuelaing.ioc.container;

import co.edu.escuelaing.ioc.annotations.RestController;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerScanner {

    private static final Logger LOGGER = Logger.getLogger(ControllerScanner.class.getName());
    private static final String CLASS_FILE_SUFFIX = ".class";
    private final String basePackage;

    public ControllerScanner(String basePackage) {
        this.basePackage = validateBasePackage(basePackage);
    }

    public List<Class<?>> scan() {

        List<Class<?>> controllers = new ArrayList<>();

        try {

            String path = basePackage.replace('.', '/');
            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();

            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                switch (resource.getProtocol()) {
                    case "file" -> scanDirectory(resource, controllers);
                    case "jar" -> scanJar(resource, path, controllers);
                    default -> LOGGER.log(Level.FINE,
                            "Unsupported protocol: {0}", resource.getProtocol());
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error scanning package: " + basePackage);
        }

        controllers.sort(Comparator.comparing(Class::getName));
        return Collections.unmodifiableList(controllers);
    }

    private void scanDirectory(URL resource,
                               List<Class<?>> controllers) {
        scanDirectoryRecursive(resource, basePackage, controllers);
    }

    private void scanDirectoryRecursive(URL resource,
                                        String currentPackage,
                                        List<Class<?>> controllers) {
        try {
            File directory = new File(resource.toURI());
            File[] files = directory.listFiles();

            if (files == null) {
                return;
            }

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(CLASS_FILE_SUFFIX)) {
                    loadAndFilterClass(currentPackage, file.getName(), controllers);
                } else if (file.isDirectory()) {
                    String subPackage = currentPackage + "." + file.getName();
                    File subDir = new File(file.getAbsolutePath());
                    scanDirectoryRecursive(subDir.toURI().toURL(), subPackage, controllers);
                }
            }
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, e, () -> "Invalid URI for resource: " + resource);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e, () -> "Error scanning directory: " + resource);
        }
    }

    private void scanJar(URL resource,
                         String path,
                         List<Class<?>> controllers) {

        try {

            JarURLConnection connection =
                    (JarURLConnection) resource.openConnection();

            try (JarFile jarFile = connection.getJarFile()) {

                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {

                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (isCandidateClass(name, path)) {
                        loadAndFilterClassFromJar(name, controllers);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e, () -> "Error scanning JAR for package: " + basePackage);
        }
    }

    private boolean isCandidateClass(String entryName, String path) {
        return entryName.startsWith(path)
                && entryName.endsWith(CLASS_FILE_SUFFIX)
                && !entryName.contains("$");
    }

    private void loadAndFilterClass(String currentPackage,
                                    String fileName,
                                    List<Class<?>> controllers) {
        String className = currentPackage + "."
                + fileName.replace(CLASS_FILE_SUFFIX, "");

        loadAndAddIfController(className, controllers);
    }

    private void loadAndFilterClassFromJar(String entryName,
                                           List<Class<?>> controllers) {

        String className = entryName
                .replace("/", ".")
                .replace(CLASS_FILE_SUFFIX, "");

        loadAndAddIfController(className, controllers);
    }

    private void loadAndAddIfController(String className, List<Class<?>> controllers) {
        try {

            Class<?> clazz = Class.forName(
                    className,
                    false,
                    Thread.currentThread().getContextClassLoader()
            );

            if (isValidController(clazz)) {
                controllers.add(clazz);
            }

        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            LOGGER.log(Level.FINE, e, () -> "Class not loadable: " + className);
        }
    }

    private boolean isValidController(Class<?> clazz) {

        return !clazz.isInterface()
                && !clazz.isAnnotation()
                && !clazz.isEnum()
                && !clazz.isAnonymousClass()
                && !clazz.isLocalClass()
                && clazz.isAnnotationPresent(RestController.class);
    }

    private String validateBasePackage(String basePackage) {

        Objects.requireNonNull(basePackage,
                "Base package cannot be null");

        if (basePackage.isBlank()) {
            throw new IllegalArgumentException(
                    "Base package cannot be empty");
        }

        if (basePackage.contains("..")) {
            throw new IllegalArgumentException(
                    "Invalid base package path");
        }

        return basePackage;
    }
}
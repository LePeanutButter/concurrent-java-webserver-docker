package co.edu.escuelaing.ioc.container;

import co.edu.escuelaing.handlers.RouteRegistry;
import co.edu.escuelaing.http.HttpRequest;
import co.edu.escuelaing.ioc.annotations.GetMapping;
import co.edu.escuelaing.ioc.annotations.RequestParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationContext {

    private static final Logger LOGGER =
            Logger.getLogger(ApplicationContext.class.getName());

    private final String basePackage;

    public ApplicationContext(String basePackage) {
        this.basePackage = validateBasePackage(
                Objects.requireNonNull(basePackage, "Base package cannot be null"));
    }

    private static String validateBasePackage(String basePackage) {
        if (basePackage.isBlank()) {
            throw new IllegalArgumentException("Base package cannot be empty");
        }
        if (basePackage.contains("..")) {
            throw new IllegalArgumentException("Invalid base package path");
        }
        return basePackage;
    }

    public void loadControllers() {
        ControllerScanner scanner = new ControllerScanner(basePackage);
        List<Class<?>> controllers = scanner.scan();

        for (Class<?> controllerClass : controllers) {
            instantiateAndRegister(controllerClass);
        }
    }

    private void instantiateAndRegister(Class<?> controllerClass) {
        try {

            if (controllerClass.isInterface()
                    || controllerClass.isAnnotation()
                    || controllerClass.isEnum()) {
                return;
            }

            var constructor = controllerClass.getDeclaredConstructor();

            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }

            Object instance = constructor.newInstance();
            registerController(instance);

        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, "Controller must have a default constructor: {0}", controllerClass.getName());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error instantiating controller: " + controllerClass.getName());
        }
    }

    private void registerController(Object controllerInstance) {

        Method[] methods = controllerInstance.getClass().getDeclaredMethods();

        for (Method method : methods) {

            if (!method.isAnnotationPresent(GetMapping.class)) {
                continue;
            }

            if (!method.canAccess(controllerInstance)) {
                method.setAccessible(true);
            }

            validateMethodSignature(method);

            String path = method.getAnnotation(GetMapping.class).value();

            RouteRegistry.get(path, (req, resp) -> {
                try {
                    return invokeMethod(method, controllerInstance, req);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e, () -> "Error invoking controller method: " + method.getName());
                    return "500 Internal Server Error";
                }
            });
        }
    }

    private void validateMethodSignature(Method method) {

        if (!String.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalStateException(
                    "Controller method must return String: " + method.getName());
        }

        for (Parameter parameter : method.getParameters()) {
            if (!parameter.isAnnotationPresent(RequestParam.class)) {
                throw new IllegalStateException(
                        "All parameters must be annotated with @RequestParam in method: "
                                + method.getName());
            }
        }
    }

    private String invokeMethod(Method method, Object instance, HttpRequest request) throws InvocationTargetException, IllegalAccessException {

        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            args[i] = resolveParameter(parameters[i], request);
        }

        Object result = method.invoke(instance, args);

        return Objects.toString(result, "");
    }

    private Object resolveParameter(Parameter parameter,
                                    HttpRequest request) {

        RequestParam annotation =
                parameter.getAnnotation(RequestParam.class);

        String paramName = annotation.value();
        String defaultValue = annotation.defaultValue();

        String value = request.getValues(paramName);

        if (value == null || value.isBlank()) {
            value = defaultValue;
        }

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required request parameter missing: " + paramName);
        }

        return value;
    }
}
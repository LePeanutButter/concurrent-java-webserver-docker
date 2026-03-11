package co.edu.escuelaing.controller;

import co.edu.escuelaing.ioc.annotations.GetMapping;
import co.edu.escuelaing.ioc.annotations.RequestParam;
import co.edu.escuelaing.ioc.annotations.RestController;

@RestController
public class AppController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }

    @GetMapping("/math/constants/pi")
    public String getPi() {
        return String.valueOf(Math.PI);
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

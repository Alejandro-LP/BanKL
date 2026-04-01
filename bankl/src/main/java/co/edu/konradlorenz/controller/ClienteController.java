package co.edu.konradlorenz.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClienteController {

    @GetMapping("/test")
    public String test() {
        return "API funcionando 🔥";
    }
}
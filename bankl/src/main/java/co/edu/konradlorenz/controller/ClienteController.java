package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/registro")
    public ClienteNatural registrar(@RequestBody ClienteNatural cliente) {
        return clienteService.registrarCliente(cliente);
    }

    @PostMapping("/login")
    public ClienteNatural login(@RequestParam String usuario,
                                @RequestParam String contrasena,
                                @RequestParam int pin) {

        return clienteService.login(usuario, contrasena, pin);
    }
}
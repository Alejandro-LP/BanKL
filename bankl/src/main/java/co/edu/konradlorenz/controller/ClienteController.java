package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Cliente login(@RequestParam String usuario,
                         @RequestParam String contrasena,
                         @RequestParam int pin) {
        return clienteService.login(usuario, contrasena, pin);
    }

    
    @GetMapping("/cuentas")
    public List<Cuenta> obtenerCuentas(@RequestParam String usuario) {
        return clienteService.obtenerCuentas(usuario);
    }
}
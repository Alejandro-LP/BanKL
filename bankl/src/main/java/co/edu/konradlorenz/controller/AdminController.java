package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.repository.ClienteNaturalRepository;
import co.edu.konradlorenz.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ClienteNaturalRepository clienteNaturalRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/clientes")
    public List<ClienteNatural> obtenerClientesNaturales() {
        return clienteNaturalRepository.findAll();
    }

    @PutMapping("/clientes/{idDB}")
    public ClienteNatural editarCliente(@PathVariable Integer idDB,
                                        @RequestBody Map<String, String> datos) {
        ClienteNatural cliente = clienteNaturalRepository.findById(idDB)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (datos.containsKey("nombres"))   cliente.setNombres(datos.get("nombres"));
        if (datos.containsKey("apellidos")) cliente.setApellidos(datos.get("apellidos"));
        if (datos.containsKey("email"))     cliente.setEmail(datos.get("email"));
        if (datos.containsKey("telefono"))  cliente.setTelefono(datos.get("telefono"));
        if (datos.containsKey("direccion")) cliente.setDireccion(datos.get("direccion"));

        return clienteNaturalRepository.save(cliente);
    }
}
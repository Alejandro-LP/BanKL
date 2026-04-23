package co.edu.konradlorenz.controller;
 
import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.repository.ClienteNaturalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
 
    @Autowired
    private ClienteNaturalRepository clienteNaturalRepository;
 
    
    @GetMapping("/clientes")
    public List<ClienteNatural> obtenerClientesNaturales() {
        return clienteNaturalRepository.findAll();
    }
}

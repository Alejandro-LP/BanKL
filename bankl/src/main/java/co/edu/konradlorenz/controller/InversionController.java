package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Inversion;
import co.edu.konradlorenz.service.InversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/inversiones")
public class InversionController {
    @Autowired private InversionService inversionService;

    @PostMapping
    public Inversion crear(@RequestParam String usuario, @RequestParam double monto,
                           @RequestParam double tasa, @RequestParam int plazo,
                           @RequestParam String tipo) {
        return inversionService.crear(usuario, monto, tasa, plazo, tipo);
    }

    @GetMapping
    public List<Inversion> listar(@RequestParam String usuario) {
        return inversionService.listar(usuario);
    }

    @PostMapping("/retirar/{id}")
    public boolean retirar(@PathVariable Integer id) {
        return inversionService.retirar(id);
    }
}
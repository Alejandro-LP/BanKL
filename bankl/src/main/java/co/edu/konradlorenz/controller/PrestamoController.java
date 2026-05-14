package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Prestamo;
import co.edu.konradlorenz.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/prestamos")
public class PrestamoController {
    @Autowired private PrestamoService prestamoService;

    @PostMapping("/solicitar")
    public Prestamo solicitar(@RequestParam String usuario, @RequestParam double monto,
                              @RequestParam double tasa, @RequestParam int cuotas) {
        return prestamoService.solicitar(usuario, monto, tasa, cuotas);
    }

    @GetMapping
    public List<Prestamo> listar(@RequestParam String usuario) {
        return prestamoService.listar(usuario);
    }

    @PostMapping("/pagar/{id}")
    public boolean pagar(@PathVariable Integer id) {
        return prestamoService.pagarCuota(id);
    }
}
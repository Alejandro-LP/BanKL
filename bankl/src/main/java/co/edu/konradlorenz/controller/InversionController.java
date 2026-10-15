package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Inversion;
import co.edu.konradlorenz.service.InversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/inversiones")
public class InversionController {
    @Autowired private InversionService inversionService;

    @PostMapping
    public Inversion crear(@RequestParam double monto,
                           @RequestParam double tasa, @RequestParam int plazo,
                           @RequestParam String tipo, @RequestParam Integer idCuenta,
                           HttpSession session) {
        return inversionService.crear(usuario(session), idCuenta, monto, tasa, plazo, tipo);
    }

    @GetMapping
    public List<Inversion> listar(HttpSession session) {
        return inversionService.listar(usuario(session));
    }

    @PostMapping("/retirar/{id}")
    public boolean retirar(@PathVariable Integer id, @RequestParam Integer idCuenta, HttpSession session) {
        return inversionService.retirar(usuario(session), id, idCuenta);
    }

    private String usuario(HttpSession session) { return (String) session.getAttribute("usuario"); }
}

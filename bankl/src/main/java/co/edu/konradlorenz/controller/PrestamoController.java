package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Prestamo;
import co.edu.konradlorenz.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {
    @Autowired private PrestamoService prestamoService;

    @PostMapping("/solicitar")
    public Prestamo solicitar(@RequestParam double monto,
                              @RequestParam double tasa, @RequestParam int cuotas, HttpSession session) {
        return prestamoService.solicitar(usuario(session), monto, tasa, cuotas);
    }

    @GetMapping
    public List<Prestamo> listar(HttpSession session) {
        return prestamoService.listar(usuario(session));
    }

    @PostMapping("/pagar/{id}")
    public boolean pagar(@PathVariable Integer id, @RequestParam Integer idCuenta, HttpSession session) {
        return prestamoService.pagarCuota(usuario(session), id, idCuenta);
    }

    private String usuario(HttpSession session) { return (String) session.getAttribute("usuario"); }
}

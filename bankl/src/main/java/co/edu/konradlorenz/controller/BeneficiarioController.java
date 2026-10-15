package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Beneficiario;
import co.edu.konradlorenz.service.BeneficiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/beneficiarios")
public class BeneficiarioController {
    @Autowired private BeneficiarioService beneficiarioService;

    @PostMapping
    public Beneficiario agregar(@RequestParam String nombre,
                                @RequestParam String numeroCuenta, @RequestParam String banco,
                                @RequestParam String alias, HttpSession session) {
        return beneficiarioService.agregar(usuario(session), nombre, numeroCuenta, banco, alias);
    }

    @GetMapping
    public List<Beneficiario> listar(HttpSession session) {
        return beneficiarioService.listar(usuario(session));
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id, HttpSession session) {
        beneficiarioService.eliminar(usuario(session), id);
    }

    private String usuario(HttpSession session) { return (String) session.getAttribute("usuario"); }
}

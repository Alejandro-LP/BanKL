package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Beneficiario;
import co.edu.konradlorenz.service.BeneficiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/beneficiarios")
public class BeneficiarioController {
    @Autowired private BeneficiarioService beneficiarioService;

    @PostMapping
    public Beneficiario agregar(@RequestParam String usuario, @RequestParam String nombre,
                                @RequestParam String numeroCuenta, @RequestParam String banco,
                                @RequestParam String alias) {
        return beneficiarioService.agregar(usuario, nombre, numeroCuenta, banco, alias);
    }

    @GetMapping
    public List<Beneficiario> listar(@RequestParam String usuario) {
        return beneficiarioService.listar(usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        beneficiarioService.eliminar(id);
    }
}
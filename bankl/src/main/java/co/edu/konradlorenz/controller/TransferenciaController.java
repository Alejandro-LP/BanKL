package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Transferencia;
import co.edu.konradlorenz.service.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {
    @Autowired private TransferenciaService transferenciaService;

    @PostMapping
    public Transferencia realizar(@RequestParam String usuario, @RequestParam String cuentaOrigen,
                                  @RequestParam String cuentaDestino, @RequestParam String destinatario,
                                  @RequestParam double monto, @RequestParam String descripcion) {
        return transferenciaService.realizar(usuario, cuentaOrigen, cuentaDestino, destinatario, monto, descripcion);
    }

    @GetMapping
    public List<Transferencia> listar(@RequestParam String usuario) {
        return transferenciaService.listar(usuario);
    }
}
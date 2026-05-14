package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping("/consignar")
    public String consignar(@RequestParam Integer idCuenta, @RequestParam double valor) {
        return cuentaService.consignar(idCuenta, valor) ? "OK" : "Error";
    }

    @PostMapping("/retirar")
    public String retirar(@RequestParam Integer idCuenta, @RequestParam double valor) {
        return cuentaService.retirar(idCuenta, valor) ? "OK" : "Fondos insuficientes";
    }

    @PostMapping("/cambiar")
    public String cambiar(@RequestParam Integer idCuenta) {
        return cuentaService.regenerarTarjeta(idCuenta) ? "OK" : "Error";
    }
}
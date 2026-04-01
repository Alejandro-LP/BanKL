package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping("/consignar")
    public String consignar(@RequestParam Integer idCuenta,
                            @RequestParam double valor) {

        boolean ok = cuentaService.consignar(idCuenta, valor);

        return ok ? "Consignación exitosa" : "Error en consignación";
    }

    @PostMapping("/retirar")
    public String retirar(@RequestParam Integer idCuenta,
                          @RequestParam double valor) {

        boolean ok = cuentaService.retirar(idCuenta, valor);

        return ok ? "Retiro exitoso" : "Error en retiro";
    }
}
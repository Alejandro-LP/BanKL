package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping("/consignar")
    public String consignar(@RequestParam Integer idCuenta, @RequestParam double valor, HttpSession session) {
        return cuentaService.consignar(usuario(session), idCuenta, valor) ? "OK" : "Error";
    }

    @PostMapping("/pagar-tarjeta")
    public void pagarTarjeta(@RequestParam Integer idCuentaCredito, @RequestParam Integer idCuentaOrigen,
                             @RequestParam double valor, HttpSession session) {
        cuentaService.pagarTarjeta(usuario(session), idCuentaCredito, idCuentaOrigen, valor);
    }

    @PostMapping("/retirar")
    public String retirar(@RequestParam Integer idCuenta, @RequestParam double valor, HttpSession session) {
        return cuentaService.retirar(usuario(session), idCuenta, valor) ? "OK" : "Fondos insuficientes";
    }

    @PostMapping("/cambiar")
    public String cambiar(@RequestParam Integer idCuenta, HttpSession session) {
        return cuentaService.regenerarTarjeta(usuario(session), idCuenta) ? "OK" : "Error";
    }

    private String usuario(HttpSession session) { return (String) session.getAttribute("usuario"); }
}

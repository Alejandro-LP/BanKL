package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import co.edu.konradlorenz.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private CuentaService servicio;

    private final AlertasBancarias alertasBancarias = new AlertasBancarias();

    private ClienteNatural clienteActual;
    
    @PostMapping("/auth/login")
    public boolean login(@RequestParam String usuario,
                         @RequestParam String contrasena,
                         @RequestParam int pin) {

        ClienteNatural c = clienteRepo.findByUsuarioIS(usuario);

        if (c != null && c.iniciarSesion(usuario, contrasena, pin)) {
            clienteActual = c;
            return true;
        }

        return false;
    }

    @PostMapping("/auth/register")
    public String register(@RequestBody ClienteNatural cliente) {

        if (cliente == null) return "Error";

        clienteRepo.save(cliente);
        return "OK";
    }

    @GetMapping("/clientes/cuentas")
    public List<Map<String, Object>> cuentas() {

        if (clienteActual == null) return new ArrayList<>();

        List<Map<String, Object>> lista = new ArrayList<>();

        for (Cuenta c : clienteActual.getCuentas()) {

            Map<String, Object> data = new HashMap<>();

            data.put("id", c.getId());
            data.put("numero", c.getNumeroTarjeta());
            data.put("saldo", c.getSaldo());
            data.put("vencimiento", c.getFechaExpiracion());

            if (c instanceof TarjetaCredito tc) {
                data.put("tipo", "CREDITO");
                data.put("cupo", tc.getCupo());
            } else {
                data.put("tipo", "DEBITO");
            }

            lista.add(data);
        }

        return lista;
    }

    @PostMapping("/cuentas/consignar")
    public String consignar(@RequestParam int id,
                           @RequestParam double valor) {

        Cuenta c = servicio.buscarCuenta(id);

        if (c == null) return "Cuenta no encontrada";

        c.consignar(valor);

        alertasBancarias.registrarAlerta("TRANSACCION",
                "Consignación $" + valor);

        return "OK";
    }

    @PostMapping("/cuentas/retirar")
    public String retirar(@RequestParam int id,
                          @RequestParam double valor) {

        Cuenta c = servicio.buscarCuenta(id);

        if (c == null) return "Cuenta no encontrada";

        c.retirar(valor);

        alertasBancarias.registrarAlerta("TRANSACCION",
                "Retiro $" + valor);

        return "OK";
    }

    @PostMapping("/cuentas/pagar")
    public String pagar(@RequestParam int id,
                        @RequestParam double valor) {

        Cuenta cuenta = servicio.buscarCuenta(id);

        if (!(cuenta instanceof TarjetaCredito tc)) {
            return "No es tarjeta de crédito";
        }

        tc.consignar(valor);

        alertasBancarias.registrarAlerta("PAGO",
                "Pago $" + valor);

        return "OK";
    }

    @PostMapping("/cuentas/avance")
    public String avance(@RequestParam int id,
                         @RequestParam double valor) {

        Cuenta cuenta = servicio.buscarCuenta(id);

        if (!(cuenta instanceof TarjetaCredito tc)) {
            return "No es tarjeta de crédito";
        }

        tc.retirar(valor);

        alertasBancarias.registrarAlerta("TRANSACCION",
                "Avance $" + valor);

        return "OK";
    }

    @PostMapping("/cuentas/cambiar")
    public String cambiar(@RequestParam int id) {

        Cuenta c = servicio.buscarCuenta(id);

        if (c == null) return "Cuenta no encontrada";

        c.bloquearTarjeta();
        c.regenerarTarjeta();

        alertasBancarias.registrarAlerta("SEGURIDAD",
                "Tarjeta regenerada");

        return "OK";
    }

    @GetMapping("/alertas")
    public List<Alerta> alertas() {
        return alertasBancarias.revisarAlertas();
    }

    @PostMapping("/admin/login")
    public boolean loginAdmin(@RequestParam String usuario,
                             @RequestParam String contrasena) {

        return usuario.equals("admin") && contrasena.equals("1234");
    }

    @GetMapping("/admin/clientes")
    public List<Map<String, String>> clientes() {

        List<Map<String, String>> lista = new ArrayList<>();

        for (Cliente c : clienteRepo.findAll()) {

            Map<String, String> data = new HashMap<>();
            data.put("id", String.valueOf(c.getId()));
            data.put("nombre", c.getNombres());

            lista.add(data);
        }

        return lista;
    }

    // ================= ADMIN BLOQUEAR =================
    @PostMapping("/admin/bloquear")
    public String bloquear(@RequestParam int id) {

        Optional<ClienteNatural> clienteOpt = clienteRepo.findById(id);

        if (clienteOpt.isEmpty()) return "Cliente no encontrado";

        ClienteNatural cliente = clienteOpt.get();

        for (Cuenta cuenta : cliente.getCuentas()) {
            cuenta.bloquearTarjeta();
            cuenta.regenerarTarjeta();
        }

        clienteRepo.save(cliente);

        alertasBancarias.registrarAlerta("SEGURIDAD",
                "Bloqueo por admin");

        return "OK";
    }

    // ================= STATS =================
    @GetMapping("/admin/stats")
    public Map<String, Object> stats() {

        List<ClienteNatural> clientes = clienteRepo.findAll();

        int totalClientes = clientes.size();
        int totalCuentas = 0;
        double totalDinero = 0;

        for (ClienteNatural c : clientes) {

            totalCuentas += c.getCuentas().size();

            for (Cuenta cuenta : c.getCuentas()) {
                totalDinero += cuenta.getSaldo();
            }
        }

        int totalAlertas = alertasBancarias.revisarAlertas().size();

        Map<String, Object> data = new HashMap<>();
        data.put("clientes", totalClientes);
        data.put("cuentas", totalCuentas);
        data.put("dinero", totalDinero);
        data.put("alertas", totalAlertas);

        return data;
    }
}
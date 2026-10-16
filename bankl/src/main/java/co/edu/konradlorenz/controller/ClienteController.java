package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/registro")
    public Map<String, Object> registrar(@RequestBody ClienteNatural cliente) {
        ClienteNatural saved=clienteService.registrarCliente(cliente);
        return Map.of("usuarioIS", saved.getUsuarioIS(), "tipo", saved.getTipo(), "pinSeguridad", saved.getPinSeguridad());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest credentials, HttpServletRequest request) {
        if (credentials == null || credentials.usuario() == null || credentials.contrasena() == null || credentials.pin() == null)
            return Map.of();
        Cliente cliente=clienteService.login(credentials.usuario(), credentials.contrasena(), credentials.pin());
        if (cliente == null) return Map.of();
        HttpSession session=request.getSession(true);
        request.changeSessionId();
        session.setAttribute("usuario", cliente.getUsuarioIS());
        session.setAttribute("tipo", cliente.getTipo());
        return Map.of("idDB", cliente.getIdDB(), "usuarioIS", cliente.getUsuarioIS(), "tipo", cliente.getTipo(),
            "nombres", cliente.getNombres(), "apellidos", cliente.getApellidos());
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) { HttpSession s=request.getSession(false); if(s!=null)s.invalidate(); }

    public record LoginRequest(String usuario, String contrasena, Integer pin) {}

    
    @GetMapping("/cuentas")
    public List<Cuenta> obtenerCuentas(HttpSession session) {
        return clienteService.obtenerCuentas((String) session.getAttribute("usuario"));
    }

    @GetMapping("/csrf")
    public java.util.Map<String, String> csrf(HttpSession session) {
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute("csrfToken", token);
        }
        return java.util.Map.of("token", token);
    }
}

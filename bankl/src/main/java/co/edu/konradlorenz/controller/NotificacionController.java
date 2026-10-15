package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Notificacion;
import co.edu.konradlorenz.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired private NotificacionService notificacionService;

    @GetMapping
    public List<Notificacion> listar(HttpSession session) {
        return notificacionService.listar(usuario(session));
    }

    @PostMapping("/leer/{id}")
    public void marcarLeida(@PathVariable Integer id, HttpSession session) {
        notificacionService.marcarLeida(usuario(session), id);
    }

    private String usuario(HttpSession session) { return (String) session.getAttribute("usuario"); }
}

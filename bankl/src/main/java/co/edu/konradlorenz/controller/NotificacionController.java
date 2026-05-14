package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.Notificacion;
import co.edu.konradlorenz.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired private NotificacionService notificacionService;

    @GetMapping
    public List<Notificacion> listar(@RequestParam String usuario) {
        return notificacionService.listar(usuario);
    }

    @PostMapping("/leer/{id}")
    public void marcarLeida(@PathVariable Integer id) {
        notificacionService.marcarLeida(id);
    }
}
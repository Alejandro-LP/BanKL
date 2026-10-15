package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Centralizes resolution of the authenticated customer and resource ownership checks. */
@Component
public class OwnershipGuard {
    private final ClienteRepository clienteRepository;

    public OwnershipGuard(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente requireCustomer(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida");
        }
        Cliente cliente = clienteRepository.findByUsuarioIS(username);
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida");
        }
        return cliente;
    }

    public void requireOwner(Cliente authenticated, Cliente resourceOwner) {
        if (resourceOwner == null || authenticated.getIdDB() == null ||
                !authenticated.getIdDB().equals(resourceOwner.getIdDB())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este recurso");
        }
    }
}

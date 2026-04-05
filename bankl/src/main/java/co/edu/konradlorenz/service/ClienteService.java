package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // 🔥 REGISTRO
    public Cliente registrarCliente(Cliente cliente) {

        if (cliente.getNombres() == null || cliente.getNombres().isEmpty() ||
            cliente.getApellidos() == null || cliente.getApellidos().isEmpty() ||
            cliente.getId() == null || cliente.getId().isEmpty() ||
            cliente.getDireccion() == null || cliente.getDireccion().isEmpty() ||
            cliente.getTelefono() == null || cliente.getTelefono().isEmpty() ||
            cliente.getEmail() == null || cliente.getEmail().isEmpty() ||
            cliente.getUsuarioIS() == null || cliente.getUsuarioIS().isEmpty() ||
            cliente.getContrasena() == null || cliente.getContrasena().isEmpty()) {

            throw new RuntimeException("Todos los campos son obligatorios");
        }

        return clienteRepository.save(cliente);
    }

    // 🔥 LOGIN
    public Cliente login(String usuario, String contrasena, int pin) {

        Cliente cliente = clienteRepository.findByUsuarioIS(usuario);

        if (cliente != null &&
            cliente.getContrasena().equals(contrasena) &&
            cliente.getPinSeguridad() == pin) {

            return cliente;
        }

        return null;
    }

    public Cliente buscarPorId(Integer id) {
        return clienteRepository.findById(id).orElse(null);
    }
}
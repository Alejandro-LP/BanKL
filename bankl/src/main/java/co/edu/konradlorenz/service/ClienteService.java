package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteNatural registrarCliente(ClienteNatural cliente) {
        return clienteRepository.save(cliente);
    }

    public ClienteNatural login(String usuario, String contrasena, int pin) {

        ClienteNatural cliente = clienteRepository.findByUsuarioIS(usuario);

        if (cliente != null &&
            cliente.getContrasena().equals(contrasena) &&
            cliente.getPinSeguridad() == pin) {

            return cliente;
        }

        return null;
    }

    public ClienteNatural buscarPorId(Integer id) {
        return clienteRepository.findById(id).orElse(null);
    }
}
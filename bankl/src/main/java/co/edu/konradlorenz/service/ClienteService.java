package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.repository.ClienteRepository;
import co.edu.konradlorenz.repository.ClienteNaturalRepository;
import co.edu.konradlorenz.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteNaturalRepository clienteNaturalRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    public ClienteNatural registrarCliente(ClienteNatural cliente) {

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


        ClienteNatural guardado = clienteNaturalRepository.save(cliente);


        String nombre = guardado.getNombres() + " " + guardado.getApellidos();

        Cuenta debito = Cuenta.crearDebito(nombre);
        debito.setCliente(guardado);
        cuentaRepository.save(debito);

        Cuenta credito = Cuenta.crearCredito(nombre);
        credito.setCliente(guardado);
        cuentaRepository.save(credito);

        return guardado;
    }


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

    public List<Cuenta> obtenerCuentas(String usuarioIS) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) return List.of();
        return cuentaRepository.findByClienteIdDB(cliente.getIdDB());
    }
}
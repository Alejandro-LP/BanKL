package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cliente;
import co.edu.konradlorenz.model.ClienteNatural;
import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.repository.ClienteRepository;
import co.edu.konradlorenz.repository.ClienteNaturalRepository;
import co.edu.konradlorenz.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteNaturalRepository clienteNaturalRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Transactional
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
        if (cliente.getNombres().length() > 80 || cliente.getApellidos().length() > 80 ||
            cliente.getId().length() > 32 || cliente.getDireccion().length() > 180 ||
            cliente.getTelefono().length() > 24 || cliente.getEmail().length() > 254 ||
            cliente.getUsuarioIS().length() > 40 || !cliente.getUsuarioIS().matches("[A-Za-z0-9._-]+") ||
            !cliente.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Datos de registro inválidos");
        }


        if (clienteRepository.findByUsuarioIS(cliente.getUsuarioIS()) != null) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        if (!cliente.getContrasena().matches("^(?=.*[A-Z])(?=.*\\d).{10,}$")) {
            throw new IllegalArgumentException("La contraseña debe tener 10 caracteres, una mayúscula y un número");
        }
        cliente.setContrasena(hashPassword(cliente.getContrasena()));
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
        if (cliente != null && verifyPassword(contrasena, cliente.getContrasena()) &&
            cliente.getPinSeguridad() == pin) {
            if (!cliente.getContrasena().startsWith("{pbkdf2}")) {
                cliente.setContrasena(hashPassword(contrasena));
                clienteRepository.save(cliente);
            }
            return cliente;
        }
        return null;
    }

    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 210_000, 256);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return "{pbkdf2}" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) { throw new IllegalStateException("No se pudo proteger la contraseña", ex); }
    }

    private boolean verifyPassword(String password, String stored) {
        if (stored == null || password == null) return false;
        if (!stored.startsWith("{pbkdf2}")) return java.security.MessageDigest.isEqual(
            stored.getBytes(java.nio.charset.StandardCharsets.UTF_8), password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try {
            String[] parts = stored.substring(8).split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(new PBEKeySpec(password.toCharArray(), salt, 210_000, 256)).getEncoded();
            return java.security.MessageDigest.isEqual(expected, actual);
        } catch (Exception ex) { return false; }
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

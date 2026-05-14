package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BeneficiarioService {
    @Autowired private BeneficiarioRepository beneficiarioRepository;
    @Autowired private ClienteRepository clienteRepository;

    public Beneficiario agregar(String usuarioIS, String nombre, String numeroCuenta, String banco, String alias) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) throw new RuntimeException("Cliente no encontrado");
        Beneficiario b = new Beneficiario();
        b.setNombre(nombre);
        b.setNumeroCuenta(numeroCuenta);
        b.setBanco(banco);
        b.setAlias(alias);
        b.setCliente(cliente);
        return beneficiarioRepository.save(b);
    }

    public List<Beneficiario> listar(String usuarioIS) {
        Cliente cliente = clienteRepository.findByUsuarioIS(usuarioIS);
        if (cliente == null) return List.of();
        return beneficiarioRepository.findByClienteIdDB(cliente.getIdDB());
    }

    public void eliminar(Integer id) { beneficiarioRepository.deleteById(id); }
}
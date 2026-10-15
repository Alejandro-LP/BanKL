package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.*;
import co.edu.konradlorenz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class BeneficiarioService {
    private final BeneficiarioRepository beneficiarioRepository;
    private final OwnershipGuard ownershipGuard;

    public BeneficiarioService(BeneficiarioRepository beneficiarioRepository, OwnershipGuard ownershipGuard) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.ownershipGuard = ownershipGuard;
    }

    public Beneficiario agregar(String usuarioIS, String nombre, String numeroCuenta, String banco, String alias) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Beneficiario b = new Beneficiario();
        b.setNombre(nombre);
        b.setNumeroCuenta(numeroCuenta);
        b.setBanco(banco);
        b.setAlias(alias);
        b.setCliente(cliente);
        return beneficiarioRepository.save(b);
    }

    public List<Beneficiario> listar(String usuarioIS) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        return beneficiarioRepository.findByClienteIdDB(cliente.getIdDB());
    }

    public void eliminar(String usuarioIS, Integer id) {
        Cliente cliente = ownershipGuard.requireCustomer(usuarioIS);
        Beneficiario beneficiario = beneficiarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiario no encontrado"));
        ownershipGuard.requireOwner(cliente, beneficiario.getCliente());
        beneficiarioRepository.delete(beneficiario);
    }
}

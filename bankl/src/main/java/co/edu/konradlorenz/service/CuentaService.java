package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository repo;

    public Cuenta buscarCuenta(int id) {
        return repo.findById(id).orElse(null);
    }
}
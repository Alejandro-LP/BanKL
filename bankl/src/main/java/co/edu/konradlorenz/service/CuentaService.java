package co.edu.konradlorenz.service;

import co.edu.konradlorenz.model.Cuenta;
import co.edu.konradlorenz.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    public Cuenta guardarCuenta(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public Cuenta buscarCuenta(Integer id) {
        return cuentaRepository.findById(id).orElse(null);
    }

    public boolean consignar(Integer idCuenta, double valor) {
        Cuenta cuenta = buscarCuenta(idCuenta);
        if (cuenta != null) {
            boolean ok = cuenta.consignar(valor);
            cuentaRepository.save(cuenta);
            return ok;
        }
        return false;
    }

    public boolean retirar(Integer idCuenta, double valor) {
        Cuenta cuenta = buscarCuenta(idCuenta);
        if (cuenta != null) {
            boolean ok = cuenta.retirar(valor);
            cuentaRepository.save(cuenta);
            return ok;
        }
        return false;
    }

    public boolean regenerarTarjeta(Integer idCuenta) {
        Cuenta cuenta = buscarCuenta(idCuenta);
        if (cuenta != null) {
            cuenta.regenerarTarjeta();
            cuentaRepository.save(cuenta);
            return true;
        }
        return false;
    }
}
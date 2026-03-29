package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaDebito extends Cuenta {

    private List<String> alertas = new ArrayList<>();

    public TarjetaDebito() {}

    public TarjetaDebito(int numeroCuenta, String propietario, double saldo, String numeroTarjeta, String fechaExpiracion, int cvv) {
        super(numeroCuenta, propietario, saldo, numeroTarjeta, fechaExpiracion, cvv);
    }

    public List<String> getAlertas() {
        return new ArrayList<>(alertas);
    }

    public void setAlertas(List<String> alertas) {
        this.alertas = new ArrayList<>(alertas);
    }

    @Override
    public boolean consignar(double valor) {

        if (!aumentarSaldo(valor)) {
            alertas.add("Error en depósito: valor inválido.");
            return false;
        }

        alertas.add("Depósito en tarjeta débito: " + valor);
        return true;
    }

    @Override
    public boolean retirar(double valor) {

        if (!disminuirSaldo(valor)) {
            alertas.add("Error en retiro: fondos insuficientes o valor inválido.");
            return false;
        }

        alertas.add("Retiro en tarjeta débito: " + valor);
        return true;
    }
}
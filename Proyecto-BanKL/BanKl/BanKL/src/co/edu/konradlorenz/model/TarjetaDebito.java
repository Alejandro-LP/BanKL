package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaDebito extends Cuenta {

    private List<String> alertas = new ArrayList<>();

    public TarjetaDebito() {}

    public TarjetaDebito(int numeroCuenta, String propietario, double saldo,
                         String numeroTarjeta, String fechaExpiracion, int cvv) {

        super(numeroCuenta, propietario, saldo, numeroTarjeta, fechaExpiracion, cvv);
    }

    @Override
    public boolean consignar(double valor) {

        if (isBloqueada()) {
            alertas.add("Tarjeta bloqueada");
            return false;
        }

        if (!aumentarSaldo(valor)) {
            alertas.add("Error en depósito");
            return false;
        }

        alertas.add("Depósito: " + valor);
        return true;
    }

    @Override
    public boolean retirar(double valor) {

        if (isBloqueada()) {
            alertas.add("Tarjeta bloqueada");
            return false;
        }

        if (!disminuirSaldo(valor)) {
            alertas.add("Error en retiro");
            return false;
        }

        alertas.add("Retiro: " + valor);
        return true;
    }

    @Override
    protected void registrarAlerta(String mensaje) {
        alertas.add(mensaje);
    }

    public List<String> getAlertas() {
        return new ArrayList<>(alertas);
    }
}
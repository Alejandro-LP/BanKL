package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaDebito extends Cuenta {

    private AlertasBancarias alertas = new AlertasBancarias();

    public TarjetaDebito() {}

    public TarjetaDebito(int numeroCuenta, String propietario, double saldo,
                         String numeroTarjeta, String fechaExpiracion, int cvv) {

        super(numeroCuenta, propietario, saldo, numeroTarjeta, fechaExpiracion, cvv);
    }

    @Override
    public boolean consignar(double valor) {

        if (isBloqueada()) {
            registrarAlerta("ERROR", "Tarjeta bloqueada");
            return false;
        }

        if (!aumentarSaldo(valor)) {
            registrarAlerta("ERROR", "Error en depósito");
            return false;
        }

        registrarTransaccion("DEPOSITO", valor);
        registrarAlerta("TRANSACCION", "Depósito: " + valor);
        return true;
    }

    @Override
    public boolean retirar(double valor) {

        if (isBloqueada()) {
            registrarAlerta("ERROR", "Tarjeta bloqueada");
            return false;
        }

        if (!disminuirSaldo(valor)) {
            registrarAlerta("ERROR", "Error en retiro");
            return false;
        }

        registrarTransaccion("RETIRO", valor);
        registrarAlerta("TRANSACCION", "Retiro: " + valor);
        return true;
    }

    @Override
    protected void registrarAlerta(String tipo, String descripcion) {
        alertas.registrarAlerta(tipo, descripcion);
    }

    public List<Alerta> getAlertas() {
        return alertas.revisarAlertas();
    }
}
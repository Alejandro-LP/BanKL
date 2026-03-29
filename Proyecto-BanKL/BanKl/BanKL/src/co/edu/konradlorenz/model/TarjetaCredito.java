package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaCredito extends Cuenta {

    private double cupo;
    private List<String> alertas = new ArrayList<>();

    public TarjetaCredito() {}

    public TarjetaCredito(int numeroCuenta, String propietario, double saldo,
                          String numeroTarjeta, String fechaExpiracion, int cvv, double cupo) {

        super(numeroCuenta, propietario, saldo, numeroTarjeta, fechaExpiracion, cvv);
        this.cupo = cupo;
    }

    public double getCupo() { return cupo; }

    public double disponible() {
        return cupo + getSaldo();
    }

    @Override
    public boolean consignar(double valor) {

        if (isBloqueada()) {
            alertas.add("Tarjeta bloqueada");
            return false;
        }

        if (valor <= 0) {
            alertas.add("Valor inválido");
            return false;
        }

        if (!aumentarSaldo(valor)) {
            alertas.add("Error en pago");
            return false;
        }

        cupo += valor;
        alertas.add("Pago realizado: " + valor);
        return true;
    }

    @Override
    public boolean retirar(double valor) {

        if (isBloqueada()) {
            alertas.add("Tarjeta bloqueada");
            return false;
        }

        if (valor <= 0 || valor > cupo) {
            alertas.add("Excede cupo");
            return false;
        }

        if (!disminuirSaldo(valor)) {
            alertas.add("Error en avance");
            return false;
        }

        cupo -= valor;
        alertas.add("Avance: " + valor);
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
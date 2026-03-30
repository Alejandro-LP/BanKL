package co.edu.konradlorenz.model;

import java.util.List;

public class TarjetaCredito extends Cuenta {

    private double cupo;
    private AlertasBancarias alertas = new AlertasBancarias();

    public TarjetaCredito() {}

    public TarjetaCredito(int numeroCuenta, String propietario, double saldo,
                          String numeroTarjeta, String fechaExpiracion, int cvv, double cupo) {

        super(numeroCuenta, propietario, saldo, numeroTarjeta, fechaExpiracion, cvv);
        this.cupo = cupo;
    }

    public double getCupo() { return cupo; }

    @Override
    public boolean consignar(double valor) {

        if (isBloqueada()) {
            registrarAlerta("ERROR", "Tarjeta bloqueada");
            return false;
        }

        if (valor <= 0) {
            registrarAlerta("ERROR", "Valor inválido");
            return false;
        }

        if (!aumentarSaldo(valor)) {
            registrarAlerta("ERROR", "Error en pago");
            return false;
        }

        cupo += valor;

        registrarTransaccion("PAGO", valor);
        registrarAlerta("TRANSACCION", "Pago: " + valor);
        return true;
    }

    @Override
    public boolean retirar(double valor) {

        if (isBloqueada()) {
            registrarAlerta("ERROR", "Tarjeta bloqueada");
            return false;
        }

        if (valor <= 0 || valor > cupo) {
            registrarAlerta("ERROR", "Excede cupo");
            return false;
        }

        if (!disminuirSaldo(valor)) {
            registrarAlerta("ERROR", "Error en avance");
            return false;
        }

        cupo -= valor;

        registrarTransaccion("AVANCE", valor);
        registrarAlerta("TRANSACCION", "Avance: " + valor);
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
package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class TarjetaCredito extends Cuenta {

    private double cupo;
    private List<String> alertas = new ArrayList<>();

    public TarjetaCredito(int numeroCuenta, String propietario, double saldo, String numeroTarjeta, String fechaExpiracion, int cvv, double cupo) {
        super(numeroCuenta, propietario, saldo, numeroTarjeta, fechaExpiracion, cvv);
        this.cupo = cupo;
    }

    public TarjetaCredito() {
        super();
    }

    public double getCupo() {
        return cupo;
    }

    public void setCupo(double cupo) {
        this.cupo = cupo;
    }

    public double disponible() {
        return cupo + getSaldo();
    }

    @Override
    public boolean consignar(double valor) {
        try {
            validarTransaccion(valor, 3000000);

            if (!aumentarSaldo(valor)) {
                throw new IllegalArgumentException("No se pudo realizar el pago.");
            }

            cupo += valor;

            alertas.add("Pago realizado en tarjeta de crédito: " + valor);
            return true;

        } catch (IllegalArgumentException e) {
            alertas.add("Error en consignación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean retirar(double valor) {
        try {
            validarTransaccion(valor, 3000000);

            if (valor > cupo) {
                throw new IllegalArgumentException("El valor excede el cupo disponible.");
            }

            if (!disminuirSaldo(valor)) {
                throw new IllegalArgumentException("No se pudo realizar el avance.");
            }

            cupo -= valor;

            alertas.add("Avance en efectivo: " + valor);
            return true;

        } catch (IllegalArgumentException e) {
            alertas.add("Error en retiro: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAlertas() {
        return new ArrayList<>(alertas);
    }

    private void validarTransaccion(double valor, double limiteMaximo) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("Valor no válido.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Debe ser mayor que cero.");
        }
        if (valor > limiteMaximo) {
            throw new IllegalArgumentException("Excede el límite: " + limiteMaximo);
        }
    }
}
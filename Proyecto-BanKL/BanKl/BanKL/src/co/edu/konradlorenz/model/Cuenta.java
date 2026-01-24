package co.edu.konradlorenz.model;

public class Cuenta {
    private int numeroCuenta;
    private String propietario;
    private double saldo;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private int cvv;

    public Cuenta() {}

    public Cuenta(int numeroCuenta, String propietario, double saldo, String numeroTarjeta, String fechaExpiracion, int cvv) {
        this.numeroCuenta = numeroCuenta;
        this.propietario = propietario;
        this.saldo = saldo;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
    }

    public int getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(int numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public void consignar(double valor) {
        saldo += valor;
    }

    public boolean retirar(double valor) {
        if (valor > 0 && valor <= saldo) {
            saldo -= valor;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "numeroCuenta=" + numeroCuenta +
                ", propietario='" + propietario + '\'' +
                ", saldo=" + saldo +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", fechaExpiracion='" + fechaExpiracion + '\'' +
                ", cvv=" + cvv +
                '}';
    }
} 
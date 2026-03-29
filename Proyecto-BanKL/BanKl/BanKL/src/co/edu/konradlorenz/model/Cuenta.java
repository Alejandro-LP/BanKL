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
        this.saldo = saldo >= 0 ? saldo : 0;
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

    //MÉTODOS PROTEGIDOS

    protected boolean aumentarSaldo(double valor) {
        if (valor <= 0) return false;
        saldo += valor;
        return true;
    }

    protected boolean disminuirSaldo(double valor) {
        if (valor <= 0 || valor > saldo) return false;
        saldo -= valor;
        return true;
    }

    // ✅ MÉTODOS PÚBLICOS

    public boolean consignar(double valor) {
        return aumentarSaldo(valor);
    }

    public boolean retirar(double valor) {
        return disminuirSaldo(valor);
    }

    public String getNumeroTarjetaOculta() {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "****";
        }
        return "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
    }

    @Override
    public String toString() {
        return "Cuenta{" +
                "numeroCuenta=" + numeroCuenta +
                ", propietario='" + propietario + '\'' +
                ", saldo=" + saldo +
                ", numeroTarjeta='" + getNumeroTarjetaOculta() + '\'' +
                ", fechaExpiracion='" + fechaExpiracion + '\'' +
                '}';
    }
}
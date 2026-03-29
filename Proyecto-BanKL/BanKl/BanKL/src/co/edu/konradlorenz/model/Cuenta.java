package co.edu.konradlorenz.model;

public class Cuenta {

    private int numeroCuenta;
    private String propietario;
    private double saldo;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private int cvv;
    private boolean bloqueada;

    public Cuenta() {}

    public Cuenta(int numeroCuenta, String propietario, double saldo,
                  String numeroTarjeta, String fechaExpiracion, int cvv) {

        this.numeroCuenta = numeroCuenta;
        this.propietario = propietario;
        this.saldo = saldo >= 0 ? saldo : 0;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
        this.bloqueada = false;
    }

    public double getSaldo() { return saldo; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public boolean isBloqueada() { return bloqueada; }

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

    public void bloquearTarjeta() {
        this.bloqueada = true;
        registrarAlerta("Tarjeta bloqueada");
    }

    public void regenerarTarjeta() {

        String anterior = this.numeroTarjeta;

        this.numeroTarjeta = generarNumeroTarjeta();
        this.fechaExpiracion = generarFechaExpiracion();
        this.cvv = generarCVV();
        this.bloqueada = false;

        registrarAlerta("Tarjeta regenerada. Terminaba en "
                + anterior.substring(anterior.length() - 4));
    }

    public boolean consignar(double valor) {
        if (bloqueada) return false;
        return aumentarSaldo(valor);
    }

    public boolean retirar(double valor) {
        if (bloqueada) return false;
        return disminuirSaldo(valor);
    }

    // 🔔 método para que las hijas manejen alertas
    protected void registrarAlerta(String mensaje) {}

    // ======================
    // 🔧 GENERADORES
    // ======================

    private String generarNumeroTarjeta() {

        String[] bins = {"51", "52", "53", "54", "55"};
        String bin = bins[(int)(Math.random() * bins.length)];

        StringBuilder numero = new StringBuilder(bin);

        while (numero.length() < 15) {
            numero.append((int)(Math.random() * 10));
        }

        numero.append(calcularLuhn(numero.toString()));

        return numero.toString();
    }

    private int calcularLuhn(String numero) {

        int suma = 0;
        boolean duplicar = true;

        for (int i = numero.length() - 1; i >= 0; i--) {

            int digito = Character.getNumericValue(numero.charAt(i));

            if (duplicar) {
                digito *= 2;
                if (digito > 9) digito -= 9;
            }

            suma += digito;
            duplicar = !duplicar;
        }

        return (10 - (suma % 10)) % 10;
    }

    private int generarCVV() {
        return (int)(Math.random() * 900) + 100;
    }

    private String generarFechaExpiracion() {
        int mes = (int)(Math.random() * 12) + 1;
        int anio = (int)(Math.random() * 5) + 26;
        return String.format("%02d/%02d", mes, anio);
    }
}
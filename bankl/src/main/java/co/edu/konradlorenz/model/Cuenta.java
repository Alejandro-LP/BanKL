package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numeroCuenta;
    private String propietario;
    private double saldo;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private int cvv;
    private boolean bloqueada;
    private String tipo; // "DEBITO" o "CREDITO"
    private double cupo; // solo aplica para crédito (máx 200000)

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private List<Transaccion> historial = new ArrayList<>();

    public Cuenta() {}

    // ===== FACTORY METHODS =====

    public static Cuenta crearDebito(String propietario) {
        Cuenta c = new Cuenta();
        c.tipo = "DEBITO";
        c.propietario = propietario;
        c.saldo = 0;
        c.cupo = 0;
        c.bloqueada = false;
        c.generarDatosAleatorios();
        return c;
    }

    public static Cuenta crearCredito(String propietario) {
        Cuenta c = new Cuenta();
        c.tipo = "CREDITO";
        c.propietario = propietario;
        c.saldo = 0; // deuda acumulada
        c.bloqueada = false;
        // cupo aleatorio entre 1000 y 200000
        c.cupo = Math.round((1000 + Math.random() * 199000) * 100.0) / 100.0;
        c.generarDatosAleatorios();
        return c;
    }

    private void generarDatosAleatorios() {
        Random r = new Random();
        // número de cuenta de 8 dígitos
        this.numeroCuenta = 10000000 + r.nextInt(90000000);
        // número de tarjeta: 4 grupos de 4 dígitos
        this.numeroTarjeta = String.format("%04d %04d %04d %04d",
            r.nextInt(10000), r.nextInt(10000), r.nextInt(10000), r.nextInt(10000));
        // CVV 3 dígitos
        this.cvv = 100 + r.nextInt(900);
        // fecha expiración: entre 2 y 5 años a futuro
        int anio = 2026 + r.nextInt(4);
        int mes = 1 + r.nextInt(12);
        this.fechaExpiracion = String.format("%02d/%d", mes, anio);
    }

    // ===== GETTERS =====
    public Integer getId() { return id; }
    public int getNumeroCuenta() { return numeroCuenta; }
    public String getPropietario() { return propietario; }
    public double getSaldo() { return saldo; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public int getCvv() { return cvv; }
    public boolean isBloqueada() { return bloqueada; }
    public String getTipo() { return tipo; }
    public double getCupo() { return cupo; }
    public double getDisponible() { return tipo.equals("CREDITO") ? cupo - saldo : saldo; }
    public Cliente getCliente() { return cliente; }
    public List<Transaccion> getHistorial() { return historial; }

    // ===== SETTERS =====
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setBloqueada(boolean bloqueada) { this.bloqueada = bloqueada; }

    // ===== LÓGICA =====
    public void registrarTransaccion(String tipo, double valor) {
        Transaccion t = new Transaccion(tipo, valor);
        t.setCuenta(this);
        historial.add(t);
    }

    public boolean consignar(double valor) {
        if (bloqueada || valor <= 0) return false;
        saldo += valor;
        registrarTransaccion("CONSIGNACION", valor);
        return true;
    }

    public boolean retirar(double valor) {
        if (bloqueada || valor <= 0) return false;
        if ("DEBITO".equals(tipo) && valor > saldo) return false;
        if ("CREDITO".equals(tipo) && valor > (cupo - saldo)) return false;
        saldo += "CREDITO".equals(tipo) ? valor : -valor; // crédito sube deuda
        registrarTransaccion("RETIRO", valor);
        return true;
    }

    public void bloquearTarjeta() { this.bloqueada = true; }

    public void regenerarTarjeta() { generarDatosAleatorios(); }
}
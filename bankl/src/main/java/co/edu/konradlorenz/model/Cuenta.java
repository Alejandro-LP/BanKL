package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

   
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    private List<Transaccion> historial = new ArrayList<>();

    public Cuenta() {}

    public Cuenta(int numeroCuenta, String propietario, double saldo,
                  String numeroTarjeta, String fechaExpiracion, int cvv) {

        this.numeroCuenta = numeroCuenta;
        this.propietario = propietario;
        this.saldo = saldo;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.cvv = cvv;
        this.bloqueada = false;
    }

    // ===== GETTERS =====

    public Integer getId() { return id; }
    public double getSaldo() { return saldo; }
    public boolean isBloqueada() { return bloqueada; }
    public Cliente getCliente() { return cliente; }
    public List<Transaccion> getHistorial() { return historial; }

    // ===== SETTERS =====

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // ===== LÓGICA =====

    public void registrarTransaccion(String tipo, double valor) {
        Transaccion t = new Transaccion(tipo, valor);
        t.setCuenta(this); // 🔥 IMPORTANTE
        historial.add(t);
    }

    public boolean consignar(double valor) {
        if (bloqueada || valor <= 0) return false;
        saldo += valor;
        registrarTransaccion("CONSIGNACION", valor);
        return true;
    }

    public boolean retirar(double valor) {
        if (bloqueada || valor <= 0 || valor > saldo) return false;
        saldo -= valor;
        registrarTransaccion("RETIRO", valor);
        return true;
    }

    public void bloquearTarjeta() {
        this.bloqueada = true;
    }
}
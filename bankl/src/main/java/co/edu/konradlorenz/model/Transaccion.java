package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tipo;
    private double valor;
    private LocalDateTime fecha;

    // 🔥 RELACIÓN CON CUENTA
    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    public Transaccion() {
        this.fecha = LocalDateTime.now();
    }

    public Transaccion(String tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = LocalDateTime.now();
    }

    // ===== GETTERS =====

    public Integer getId() { return id; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDateTime getFecha() { return fecha; }

    public Cuenta getCuenta() {
        return cuenta;
    }

    // ===== SETTERS =====

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
}
package co.edu.konradlorenz.model;

import java.time.LocalDateTime;

public class Transaccion {

    private String tipo;
    private double valor;
    private LocalDateTime fecha;

    public Transaccion(String tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.fecha = LocalDateTime.now();
    }

    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return tipo + " | $" + valor + " | " + fecha;
    }
}
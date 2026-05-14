package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Entity
public class Inversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double monto;
    private double tasaInteres;   
    private int plazoMeses;
    private double rendimientoTotal; 
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private String estado;        
    private String tipo;         
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Inversion() {}

    public static Inversion crear(Cliente cliente, double monto, double tasaInteres, int plazoMeses, String tipo) {
        Inversion i = new Inversion();
        i.cliente = cliente;
        i.monto = monto;
        i.tasaInteres = tasaInteres;
        i.plazoMeses = plazoMeses;
        i.tipo = tipo;
        i.estado = "ACTIVA";
        i.fechaInicio = LocalDate.now();
        i.fechaVencimiento = LocalDate.now().plusMonths(plazoMeses);
        // Interés simple
        i.rendimientoTotal = monto * (tasaInteres / 100.0) * (plazoMeses / 12.0);
        i.rendimientoTotal = Math.round(i.rendimientoTotal * 100.0) / 100.0;
        return i;
    }

    public double getMontoFinal() { return monto + rendimientoTotal; }

    // Getters
    public Integer getId() { return id; }
    public double getMonto() { return monto; }
    public double getTasaInteres() { return tasaInteres; }
    public int getPlazoMeses() { return plazoMeses; }
    public double getRendimientoTotal() { return rendimientoTotal; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }
    public String getTipo() { return tipo; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setEstado(String estado) { this.estado = estado; }
}
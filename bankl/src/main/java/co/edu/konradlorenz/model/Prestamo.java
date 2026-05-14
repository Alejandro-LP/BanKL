package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double monto;
    private double tasaInteres;     
    private int numeroCuotas;       
    private int cuotasPagadas;
    private double valorCuota;      
    private String estado;          
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Prestamo() {}

    public static Prestamo crear(Cliente cliente, double monto, double tasaInteres, int numeroCuotas) {
        Prestamo p = new Prestamo();
        p.cliente = cliente;
        p.monto = monto;
        p.tasaInteres = tasaInteres;
        p.numeroCuotas = numeroCuotas;
        p.cuotasPagadas = 0;
        p.estado = "ACTIVO";
        p.fechaInicio = LocalDate.now();
        p.fechaVencimiento = LocalDate.now().plusMonths(numeroCuotas);
        // Cuota mensual con interés compuesto
        double tasaMensual = tasaInteres / 100.0 / 12.0;
        p.valorCuota = monto * (tasaMensual * Math.pow(1 + tasaMensual, numeroCuotas))
                              / (Math.pow(1 + tasaMensual, numeroCuotas) - 1);
        p.valorCuota = Math.round(p.valorCuota * 100.0) / 100.0;
        return p;
    }

    public boolean pagarCuota() {
        if (!estado.equals("ACTIVO")) return false;
        cuotasPagadas++;
        if (cuotasPagadas >= numeroCuotas) estado = "PAGADO";
        return true;
    }

    public double getSaldoPendiente() {
        return valorCuota * (numeroCuotas - cuotasPagadas);
    }

    // Getters
    public Integer getId() { return id; }
    public double getMonto() { return monto; }
    public double getTasaInteres() { return tasaInteres; }
    public int getNumeroCuotas() { return numeroCuotas; }
    public int getCuotasPagadas() { return cuotasPagadas; }
    public double getValorCuota() { return valorCuota; }
    public String getEstado() { return estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
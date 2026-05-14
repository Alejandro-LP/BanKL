package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double monto;
    private String descripcion;
    private LocalDateTime fecha;
    private String estado; 

    private String cuentaOrigenNumero;
    private String cuentaDestinoNumero;
    private String nombreDestinatario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Transferencia() {}

    public static Transferencia crear(Cliente cliente, String cuentaOrigen,
            String cuentaDestino, String destinatario, double monto, String descripcion) {
        Transferencia t = new Transferencia();
        t.cliente = cliente;
        t.cuentaOrigenNumero = cuentaOrigen;
        t.cuentaDestinoNumero = cuentaDestino;
        t.nombreDestinatario = destinatario;
        t.monto = monto;
        t.descripcion = descripcion;
        t.fecha = LocalDateTime.now();
        t.estado = "EXITOSA";
        return t;
    }

    // Getters
    public Integer getId() { return id; }
    public double getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public String getCuentaOrigenNumero() { return cuentaOrigenNumero; }
    public String getCuentaDestinoNumero() { return cuentaDestinoNumero; }
    public String getNombreDestinatario() { return nombreDestinatario; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void setEstado(String estado) { this.estado = estado; }
}
package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String mensaje;
    private boolean leida;
    private LocalDateTime fecha;
    private String tipo; 

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Notificacion() {}

    public static Notificacion crear(Cliente cliente, String titulo, String mensaje, String tipo) {
        Notificacion n = new Notificacion();
        n.cliente = cliente;
        n.titulo = titulo;
        n.mensaje = mensaje;
        n.tipo = tipo;
        n.leida = false;
        n.fecha = LocalDateTime.now();
        return n;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
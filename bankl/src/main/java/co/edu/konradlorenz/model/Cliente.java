package co.edu.konradlorenz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDB; 

    protected String nombres;
    protected String apellidos;
    protected String id; 

    protected String direccion;
    protected String telefono;
    protected String email;

    @Column(unique = true)
    protected String usuarioIS;

    protected String contrasena;
    protected int pinSeguridad;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    protected List<Cuenta> cuentas;



    public Cliente() {
        this.cuentas = new ArrayList<>();
        generarPinSeguridad();
    }

    public Cliente(String nombres, String apellidos, String id, String direccion,
                   String telefono, String email, String usuarioIS, String contrasena) {

        this();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.id = id;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.usuarioIS = usuarioIS;
        this.contrasena = contrasena;
    }

   

    public Integer getIdDB() { return idDB; } // 🔥 ID BD
    public String getId() { return id; } // 🔥 tu ID (cédula)

    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getUsuarioIS() { return usuarioIS; }
    public String getContrasena() { return contrasena; }
    public int getPinSeguridad() { return pinSeguridad; }

    public List<Cuenta> getCuentas() {
        return cuentas; // 🔥 IMPORTANTE (no copia)
    }

   

    public void agregarCuenta(Cuenta cuenta) {
        if (cuenta != null) {
            cuenta.setCliente(this); 
            cuentas.add(cuenta);
        }
    }

    public void generarPinSeguridad() {
        this.pinSeguridad = new Random().nextInt(9000) + 1000;
    }

    public abstract boolean iniciarSesion(String usuario, String contrasena, int pin);
}
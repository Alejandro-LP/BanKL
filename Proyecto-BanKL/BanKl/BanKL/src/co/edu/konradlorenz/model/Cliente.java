package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Cliente {

    protected String nombres;
    protected String apellidos;
    protected String id;
    protected String direccion;
    protected String telefono;
    protected String email;
    protected List<Cuenta> cuentas;
    protected String usuarioIS;
    protected String contrasena;
    protected int pinSeguridad;

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
    public String getNombres() {return nombres;}
    public String getApellidos(){return apellidos;}
    public String getId() { return id; }
    public String getUsuarioIS() { return usuarioIS; }
    public String getContrasena() { return contrasena; }
    public int getPinSeguridad() { return pinSeguridad; }

    public List<Cuenta> getCuentas() {
        return new ArrayList<>(cuentas);
    }

    public void agregarCuenta(Cuenta cuenta) {
        if (cuenta != null) {
            cuentas.add(cuenta);
        }
    }

    public void generarPinSeguridad() {
        this.pinSeguridad = new Random().nextInt(9000) + 1000;
    }

    public abstract boolean iniciarSesion(String usuario, String contrasena, int pin);
}
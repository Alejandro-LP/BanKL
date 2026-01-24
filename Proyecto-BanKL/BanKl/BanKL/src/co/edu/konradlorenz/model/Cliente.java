package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Cliente  {
    // ATRIBUTOS
    protected String nombres;
    protected String apellidos;
    protected String id;
    protected String direccion;
    protected int telefono;
    protected String email;
    protected List<Cuenta> cuentas;
    protected String usuarioIS;
    protected String contraseña;
    protected int pinSeguridad;

    // CONSTRUCTORES
    public Cliente() {
        this.cuentas = new ArrayList<>();
        generarPinSeguridad(); // Asegura que cada cliente tenga un pin
    }

    public Cliente(String nombres, String apellidos, String id, String direccion, int telefono, String email,
                   List<Cuenta> cuentas, String usuarioIS, String contraseña) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.id = id;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.cuentas = cuentas != null ? cuentas : new ArrayList<>();
        this.usuarioIS = usuarioIS;
        this.contraseña = contraseña;
        generarPinSeguridad(); // PIN aleatorio al crear
    }

    // GETTERS Y SETTERS
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public String getUsuarioIS() {
        return usuarioIS;
    }

    public void setUsuarioIS(String usuarioIS) {
        this.usuarioIS = usuarioIS;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getPinSeguridad() {
        return pinSeguridad;
    }

    public void setPinSeguridad(int pinSeguridad) {
        this.pinSeguridad = pinSeguridad;
    }

    @Override
    public String toString() {
        return "Cliente{"
                + "nombres='" + nombres + '\''
                + ", apellidos='" + apellidos + '\''
                + ", id='" + id + '\''
                + ", direccion='" + direccion + '\''
                + ", telefono=" + telefono
                + ", email='" + email + '\''
                + ", cuentas=" + cuentas
                + ", usuarioIS='" + usuarioIS + '\''
                + ", contraseña='" + contraseña + '\''
                + ", pinSeguridad=" + pinSeguridad
                + '}';
    }

    // Método que genera un PIN de seguridad aleatorio de 4 dígitos
    public void generarPinSeguridad() {
        this.pinSeguridad = new Random().nextInt(9000) + 1000; // de 1000 a 9999
    }

    // MÉTODOS ABSTRACTOS
    public abstract void registrarCliente();

    public abstract boolean iniciarSesion(String usuario, String contraseña, int pin);
}

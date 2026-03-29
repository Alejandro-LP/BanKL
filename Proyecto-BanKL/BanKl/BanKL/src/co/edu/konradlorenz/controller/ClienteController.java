package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.*;

public class ClienteController {

    private Registro registro;

    public ClienteController(Registro registro) {
        this.registro = registro;
    }

    public Cliente registrarClienteNatural(String nombres, String apellidos, String id,
                                           String direccion, String telefono, String email,
                                           String usuario, String contrasena) {

        if (registro.buscarPorId(id) != null) return null;

        ClienteNatural cliente = new ClienteNatural(
                nombres, apellidos, id, direccion,
                telefono, email, usuario, contrasena
        );

        crearCuentasIniciales(cliente);
        registro.agregarCliente(cliente);

        return cliente;
    }

    public Cliente login(String usuario, String contrasena, int pin) {

        for (Cliente c : registro.getClientes()) {
            if (c.iniciarSesion(usuario, contrasena, pin)) {
                return c;
            }
        }
        return null;
    }

    public boolean bloquearTarjeta(Cliente cliente, Cuenta cuenta) {
        if (cliente == null || cuenta == null) return false;
        cuenta.bloquearTarjeta();
        return true;
    }

    public boolean regenerarTarjeta(Cliente cliente, Cuenta cuenta) {
        if (cliente == null || cuenta == null) return false;
        cuenta.regenerarTarjeta();
        return true;
    }

    private void crearCuentasIniciales(Cliente cliente) {

        String nombre = cliente.getNombres() + " " + cliente.getApellidos();

        TarjetaDebito debito = new TarjetaDebito(
                generarNumeroCuenta(), nombre, 0.0,
                generarNumeroTarjeta(), generarFechaExpiracion(), generarCVV()
        );

        TarjetaCredito credito = new TarjetaCredito(
                generarNumeroCuenta(), nombre, 0.0,
                generarNumeroTarjeta(), generarFechaExpiracion(), generarCVV(),
                2000000
        );

        cliente.agregarCuenta(debito);
        cliente.agregarCuenta(credito);
    }

    private int generarNumeroCuenta() {
        return (int)(Math.random() * 90000000) + 10000000;
    }

    private String generarNumeroTarjeta() {
        String[] bins = {"51","52","53","54","55"};
        String bin = bins[(int)(Math.random()*bins.length)];
        StringBuilder numero = new StringBuilder(bin);

        while (numero.length() < 15) {
            numero.append((int)(Math.random()*10));
        }

        numero.append(calcularLuhn(numero.toString()));
        return numero.toString();
    }

    private int calcularLuhn(String numero) {
        int suma = 0;
        boolean duplicar = true;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));
            if (duplicar) {
                digito *= 2;
                if (digito > 9) digito -= 9;
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return (10 - (suma % 10)) % 10;
    }

    private int generarCVV() {
        return (int)(Math.random()*900)+100;
    }

    private String generarFechaExpiracion() {
        int mes = (int)(Math.random()*12)+1;
        int anio = (int)(Math.random()*5)+26;
        return String.format("%02d/%02d", mes, anio);
    }
}
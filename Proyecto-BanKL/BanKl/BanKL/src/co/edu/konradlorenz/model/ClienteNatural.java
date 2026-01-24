package co.edu.konradlorenz.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.edu.konradlorenz.view.Ventana;

public class ClienteNatural extends Cliente {

    public ClienteNatural() {
        super();
    }

    public ClienteNatural(String nombres, String apellidos, String id, String direccion, int telefono,
                          String email, List<Cuenta> cuentas, String usuarioIS, String contrasena) {
        super(nombres, apellidos, id, direccion, telefono, email, cuentas, usuarioIS, contrasena);
    }

    @Override
    public void registrarCliente() {
        TarjetaDebito debito = new TarjetaDebito(
                generarNumeroCuenta(),
                this.nombres + " " + this.apellidos,
                0.0,
                generarNumeroTarjeta(),
                generarFechaExpiracion(),
                generarCVV()
        );

        TarjetaCredito credito = new TarjetaCredito(
                generarNumeroCuenta(),
                this.nombres + " " + this.apellidos,
                0,
                generarNumeroTarjeta(),
                generarFechaExpiracion(),
                generarCVV(),
                2000000
        );

        if (this.cuentas == null) {
            this.cuentas = new ArrayList<>();
        }
        this.cuentas.add(debito);
        this.cuentas.add(credito);

        Ventana.mostrarMensaje("Cliente registrado: " + this.nombres + " " + this.apellidos +
                "\nPIN de seguridad: " + this.pinSeguridad +
                "\nTarjetas creadas:\n- Débito terminada en " + debito.getNumeroTarjeta().substring(debito.getNumeroTarjeta().length() - 4) +
                "\n- Crédito terminada en " + credito.getNumeroTarjeta().substring(credito.getNumeroTarjeta().length() - 4));
    }

    @Override
    public boolean iniciarSesion(String usuario, String contrasena, int pin) {
        return this.usuarioIS.equals(usuario)
                && this.contraseña.equals(contraseña)
                && this.pinSeguridad == pin;
    }

    public TarjetaDebito getTarjetaDebito() {
        for (Cuenta cuenta : this.cuentas) {
            if (cuenta instanceof TarjetaDebito) {
                return (TarjetaDebito) cuenta;
            }
        }
        return null;
    }

    public TarjetaCredito getTarjetaCredito() {
        for (Cuenta cuenta : this.cuentas) {
            if (cuenta instanceof TarjetaCredito) {
                return (TarjetaCredito) cuenta;
            }
        }
        return null;
    }

    private String generarNumeroTarjeta() {
        Random random = new Random();
        StringBuilder numero = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            numero.append(random.nextInt(10));
        }
        return numero.toString();
    }

    private String generarFechaExpiracion() {
        LocalDate hoy = LocalDate.now();
        LocalDate expiracion = hoy.plusYears(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiracion.format(formatter);
    }

    private int generarCVV() {
        return new Random().nextInt(900) + 100;
    }

    private int generarNumeroCuenta() {
        return new Random().nextInt(90000000) + 10000000;
    }

    public void generarPinSeguridad() {
        this.pinSeguridad = new Random().nextInt(9000) + 1000;
    }
}
package co.edu.konradlorenz.controller;

import co.edu.konradlorenz.model.*;

public class ClienteController {

    private Registro registro;
    private int intentosFallidos = 0;
    private final int MAX_INTENTOS = 3;

    public ClienteController(Registro registro) {
        this.registro = registro;
    }

    public Cliente login(String usuario, String contrasena, int pin) {

        for (Cliente c : registro.getClientes()) {

            if (c.iniciarSesion(usuario, contrasena, pin)) {
                intentosFallidos = 0;
                return c;
            }
        }

        intentosFallidos++;

        if (intentosFallidos >= MAX_INTENTOS) {
            bloquearTarjetasPorSeguridad();
            intentosFallidos = 0;
        }

        return null;
    }

    private void bloquearTarjetasPorSeguridad() {

    for (Cliente c : registro.getClientes()) {

        for (Cuenta cuenta : c.getCuentas()) {

            cuenta.bloquearTarjeta();
            cuenta.regenerarTarjeta();
        }
    }
}
}
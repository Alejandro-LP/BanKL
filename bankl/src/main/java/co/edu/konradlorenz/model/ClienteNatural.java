package co.edu.konradlorenz.model;

public class ClienteNatural extends Cliente {

    public ClienteNatural() {
        super();
    }

    public ClienteNatural(String nombres, String apellidos, String id, String direccion,
                          String telefono, String email, String usuarioIS, String contrasena) {

        super(nombres, apellidos, id, direccion, telefono, email, usuarioIS, contrasena);
    }

    @Override
    public boolean iniciarSesion(String usuario, String contrasena, int pin) {

        if (usuario == null || contrasena == null) return false;

        return usuarioIS.equals(usuario)
                && this.contrasena.equals(contrasena)
                && this.pinSeguridad == pin;
    }
}
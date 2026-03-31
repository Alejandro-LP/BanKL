package co.edu.konradlorenz.model;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmisionTarjeta {

    private SecureRandom secureRandom = new SecureRandom();
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/yy");

    public EmisionTarjeta() {}

    public String generarNumeroTarjeta() {
        StringBuilder numero = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            numero.append(secureRandom.nextInt(10));
        }
        return numero.toString();
    }

    public String asignarFechaExpiracion() {
        LocalDate fecha = LocalDate.now().plusYears(4);
        return fecha.format(dateTimeFormatter);
    }

    public int generarCVV() {
        return 100 + secureRandom.nextInt(900);
    }
}
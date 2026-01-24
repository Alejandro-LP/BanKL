package co.edu.konradlorenz.model;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EmisionTarjeta {
    //ATRIBUTOS
    private SecureRandom secureRandom;  // Generador de números aleatorios
    private DateTimeFormatter dateTimeFormatter; 
    //CONSTRUCTORES
    public EmisionTarjeta() {
    }

    public EmisionTarjeta(SecureRandom secureRandom, DateTimeFormatter dateTimeFormatter) {
        this.secureRandom = secureRandom;
        this.dateTimeFormatter = dateTimeFormatter;
    }
    //GETTERS Y SETTERS
    public SecureRandom getSecureRandom() {
        return secureRandom;
    } 

    public void setSecureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }
     // Método que genera un número de tarjeta de 16 dígitos    
    public String generarNumeroTarjeta() {
        StringBuilder numero = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            numero.append(secureRandom.nextInt(10));
        }
        return numero.toString();
    }
    // Método que asigna una fecha de expiración a la tarjeta (4 años a partir de hoy)
    public String asignarFechaExpiracion() {
        LocalDate fecha = LocalDate.now().plusYears(4);
        return fecha.format(dateTimeFormatter);
    }
    // Método que genera un CVV de 3 dígitos
    public int generarCVV() {
        return 100 + secureRandom.nextInt(900);
    }

}

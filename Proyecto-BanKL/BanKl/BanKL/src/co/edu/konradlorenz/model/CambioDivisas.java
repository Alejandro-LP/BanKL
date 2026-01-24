package co.edu.konradlorenz.model;

import static co.edu.konradlorenz.view.Ventana.mostrarMensaje;
import java.util.*;

public final class CambioDivisas {

    // Mapa que almacena las tasas de cambio entre diferentes divisas
    private final Map<String, Map<String, Double>> tasas;

    // Constructor que inicializa las tasas de cambio predeterminadas
    public CambioDivisas() {
        tasas = new HashMap<>();
        agregarTasa("USD", "EUR", 0.85);
        agregarTasa("EUR", "USD", 1.18);
        agregarTasa("USD", "COP", 4000.0);
        agregarTasa("COP", "USD", 0.00025);
        agregarTasa("COP", "EUR", 0.00021);
    }

    // Método para agregar una nueva tasa de cambio entre dos divisas
    public void agregarTasa(String origen, String destino, double tasa) {
        tasas.computeIfAbsent(origen, k -> new HashMap<>()).put(destino, tasa);
        tasas.computeIfAbsent(destino, k -> new HashMap<>()).put(origen, 1 / tasa);
    }

    // Método para consultar la tasa de cambio entre dos divisas
    public double consultarTasa(String origen, String destino) {
        if (tasas.containsKey(origen) && tasas.get(origen).containsKey(destino)) {
            return tasas.get(origen).get(destino);
        }
        return 0.0; // Retorna 0 si no hay tasa disponible
    }

    // Método para realizar un cambio de divisas
    public double realizarCambio(String origen, String destino, double cantidad) {
        double tasa = consultarTasa(origen, destino);
        if (tasa == 0.0) {
            mostrarMensaje("No hay tasa directa disponible entre " + origen + " y " + destino);
            return 0.0; // Retorna 0 si no se puede realizar el cambio
        }
        return cantidad * tasa; // Retorna la cantidad convertida
    }
}

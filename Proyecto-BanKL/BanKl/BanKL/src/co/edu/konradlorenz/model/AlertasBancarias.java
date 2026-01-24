package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class AlertasBancarias implements Alertas {

    private final List<Alerta> listaAlertas = new ArrayList<>();

    @Override
    public void registrarAlerta(String tipo, String descripcion) {
        listaAlertas.add(new Alerta(tipo, descripcion)); // ID generado automáticamente
    }

    @Override
    public List<Alerta> revisarAlertas() {
        return new ArrayList<>(listaAlertas);
    }
}

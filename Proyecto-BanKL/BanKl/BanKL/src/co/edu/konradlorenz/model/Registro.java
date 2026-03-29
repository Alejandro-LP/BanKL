package co.edu.konradlorenz.model;

import java.util.ArrayList;
import java.util.List;

public class Registro {

    private List<Cliente> clientes;

   
    public Registro() {
        this.clientes = new ArrayList<>();
    }

    public void agregarCliente(Cliente cliente) {
        if (cliente != null) {
            clientes.add(cliente);
        }
    }

  
    public Cliente buscarPorId(String id) {
        if (id == null) return null;

        for (Cliente c : clientes) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    
    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes); 
    }
}
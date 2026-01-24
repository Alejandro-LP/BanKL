package co.edu.konradlorenz.model;

public class Alerta {
    // ATRIBUTOS
    private static int contador = 0; 
    private int id;
    private String tipo;
    private String descripcion;

    // CONSTRUCTOR AUTOMÁTICO
    public Alerta(String tipo, String descripcion) {
        this.id = ++contador; // ID autoincremental
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // GETTERS Y SETTERS
    public static int getContador() {
        return contador;
    }

    public static void setContador(int contador) {
        Alerta.contador = contador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Alerta{" + "id=" + id + ", tipo=" + tipo + ", descripcion=" + descripcion + '}';
    }
}

package co.edu.konradlorenz.view;

import java.util.Scanner;

public class Ventana {

    private static final Scanner scanner = new Scanner(System.in);

    public static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public static String pedirString(String info) {
        System.out.println(info);
        return scanner.nextLine().trim();
    }

    public static int pedirEntero(String mensaje) {
        System.out.println(mensaje);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número entero válido.");
            }
        }
    }

    public static double pedirDouble(String mensaje) {
        System.out.println(mensaje);
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número decimal válido.");
            }
        }
    }
}

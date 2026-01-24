package co.edu.konradlorenz.controller;

import javax.swing.SwingUtilities;
import co.edu.konradlorenz.view.*;

public class Control {

    // Método principal que arranca el programa y muestra el menú principal
    public void run() {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
        
    }
}

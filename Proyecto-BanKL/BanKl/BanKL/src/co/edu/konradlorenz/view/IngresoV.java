package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;

public class IngresoV extends JFrame {

    private Registro registro;
    private AlertasBancarias alertasBancarias;
    private JTextField documentoField;
    private JPasswordField claveField;

    public IngresoV(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;
        this.alertasBancarias = alertasBancarias;

        setTitle("BanKL - Ingreso");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 215, 210));
        JLabel logo = new JLabel("<html><div style='font-family:sans-serif;'>"
                + "<span style='font-size:30px;'>"
                + "<b style='color:#001F4E;'>Ban</b><b style='color:#FFFFFF;'>KL</b>"
                + "</span><br>"
                + "<span style='font-size:12px;'>Innovación financiera para tu futuro</span></div></html>");
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        header.add(logo, BorderLayout.WEST);

        // Formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(60, 300, 60, 300));

        formPanel.add(createInput("Número de documento*", true));
        formPanel.add(createInput("Clave digital*", false));

        JButton loginButton = new JButton("Ingresa");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setMaximumSize(new Dimension(150, 40));
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginButton);

        // Footer
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(220, 215, 210));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footer.add(new JLabel("<html><b>Contáctanos</b><br>1234565465<br><br><b>Ubicación</b><br>Calle Falsa 123</html>"));
        footer.add(new JLabel("<html><center><b>Trabaja Con Nosotros</b><br>Puestos disponibles<br><br>Reclamos o Sugerencias</center></html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><center><b>Vigilados por</b><br>Lorem ipsum dolor sit amet</center></html>", SwingConstants.RIGHT));

        add(header, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // Acción del botón
        loginButton.addActionListener(e -> {
            String doc = documentoField.getText();
            String clave = new String(claveField.getPassword());
            if (registro.buscarClientePorDocumento(doc) == null) {
                int opcion = JOptionPane.showOptionDialog(
                        this,
                        "No encontramos un cliente con ese documento. ¿Deseas registrarte?",
                        "Usuario no registrado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new Object[]{"Registrarse", "Volver"},
                        "Registrarse"
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    dispose();
                    // CORREGIDO: Se pasan ambos parámetros como requiere RegistroV
                    new RegistroV(registro, alertasBancarias).setVisible(true);
                } else {
                    documentoField.setText("");
                    claveField.setText("");
                }
            } else if (Ingreso.autenticar(registro, doc, clave)) {
                dispose();
                new IngresoCliente(registro, alertasBancarias).setVisible(true);
            } else {
                int opcion = JOptionPane.showOptionDialog(
                        this,
                        "La clave es incorrecta. ¿Qué deseas hacer?",
                        "Error de clave",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        new Object[]{"Volver a intentar", "Cancelar"},
                        "Volver a intentar"
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    claveField.setText("");
                } else {
                    documentoField.setText("");
                    claveField.setText("");
                }
            }
        });
    }

    private JPanel createInput(String labelText, boolean isDocumento) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JComponent input;

        if (isDocumento) {
            documentoField = new JTextField();
            documentoField.setPreferredSize(new Dimension(250, 30));
            input = documentoField;
        } else {
            claveField = new JPasswordField();
            claveField.setPreferredSize(new Dimension(250, 30));
            input = claveField;
        }

        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
}


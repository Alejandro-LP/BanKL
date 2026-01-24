package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RegistroV extends JFrame {
    private Registro registro;

    private JTextField campoNombre;
    private JTextField campoDocumento;
    private JPasswordField campoClave;

    public RegistroV(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;

        setTitle("BanKL - Registro");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Encabezado
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
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 300, 20, 300));

        campoNombre = createField(formPanel, "Nombre*");
        campoDocumento = createField(formPanel, "Número de Documento*");
        campoClave = new JPasswordField();
        addLabeledField(formPanel, "Clave Digital*", campoClave);

        JButton registrarButton = new JButton("Registrar");
        registrarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registrarButton.addActionListener(e -> registrarCliente());

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registrarButton);

        // Pie de página
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(220, 215, 210));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footer.add(new JLabel("<html><b>Contáctanos</b><br>1234565465<br><br><b>Ubicación</b><br>Calle Falsa 123</html>"));
        footer.add(new JLabel("<html><center><b>Trabaja Con Nosotros</b><br>Explora oportunidades de empleo<br><br>¿Tienes reclamos o sugerencias?<br>¡Queremos escucharte!</center></html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><center><b>Vigilados por</b><br>Superintendencia Financiera de Colombia</center></html>", SwingConstants.RIGHT));

        add(header, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JTextField createField(JPanel panel, String labelText) {
        JTextField field = new JTextField();
        addLabeledField(panel, labelText, field);
        return field;
    }

    private void addLabeledField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(250, 30));
        panel.add(label);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    private void registrarCliente() {
        try {
            String nombre = campoNombre.getText().trim();
            String documento = campoDocumento.getText().trim();
            String clave = new String(campoClave.getPassword()).trim();

            if (nombre.isEmpty() || documento.isEmpty() || clave.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (registro.buscarClientePorDocumento(documento) != null) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese documento.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClienteNatural nuevoCliente = new ClienteNatural(
                    nombre, "", documento, "", 0, "", new ArrayList<>(), documento, clave
            );

            nuevoCliente.generarPinSeguridad();  
            nuevoCliente.registrarCliente();      

            registro.agregarCliente(nuevoCliente);
            registro.setClienteAutenticado(nuevoCliente);

            JOptionPane.showMessageDialog(this, "Registro exitoso. Redirigiendo a inicio.");
            dispose();
            new HomePage(registro).setVisible(true); 

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en el registro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


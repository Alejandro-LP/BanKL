package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CambioDeDivisasV extends JFrame {

    private Registro registro;
    private final CambioDivisas cambioDivisas;
    private final AlertasBancarias alertasBancarias;

    public CambioDeDivisasV(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;
        this.alertasBancarias = alertasBancarias;
        this.cambioDivisas = new CambioDivisas();

        setTitle("Divisas - BanKL");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 214, 207));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel(
                "<html><span style='font-size:28px; font-weight:bold; color:#001f4d;'>Ban</span><span style='font-size:28px; font-weight:bold;color:#ffffff;'>KL</span><br>Innovación financiera para tu futuro</html>");
        header.add(logoLabel, BorderLayout.WEST);

        JLabel welcomeLabel = new JLabel("<html><div style='text-align:right;'>HOLA,<br>BIENVENIDO</div></html>");
        header.add(welcomeLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // === CENTRO ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = new JLabel("Divisas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(100, 149, 237));
        titulo.setForeground(Color.WHITE);
        titulo.setMaximumSize(new Dimension(900, 50));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        centerPanel.add(titulo);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel instrucciones = new JLabel(
                "<html><div style='text-align:center;'>Estimado Usuario seleccione<br>la divisa que desea cambiar y<br>a la que desea que sea cambiada</div></html>");
        instrucciones.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel instruccionesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instruccionesPanel.setOpaque(false);
        instruccionesPanel.add(instrucciones);
        centerPanel.add(instruccionesPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel divisaPanel = new JPanel(new GridLayout(2, 3, 20, 10));
        divisaPanel.setOpaque(false);
        divisaPanel.setMaximumSize(new Dimension(600, 100));

        String[] monedas = { "USD", "EUR", "COP" };
        JComboBox<String> comboOrigen = new JComboBox<>(monedas);
        JComboBox<String> comboDestino = new JComboBox<>(monedas);

        JLabel flecha = new JLabel("A", SwingConstants.CENTER);
        flecha.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField cantidadOrigen = new JTextField("1");
        cantidadOrigen.setHorizontalAlignment(JTextField.CENTER);

        JTextField cantidadDestino = new JTextField();
        cantidadDestino.setHorizontalAlignment(JTextField.CENTER);
        cantidadDestino.setEditable(false);

        divisaPanel.add(comboOrigen);
        divisaPanel.add(new JLabel(""));
        divisaPanel.add(comboDestino);
        divisaPanel.add(cantidadOrigen);
        divisaPanel.add(flecha);
        divisaPanel.add(cantidadDestino);

        centerPanel.add(divisaPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setPreferredSize(new Dimension(120, 40));
        btnAceptar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAceptar.addActionListener((ActionEvent e) -> {
            try {
                String origen = (String) comboOrigen.getSelectedItem();
                String destino = (String) comboDestino.getSelectedItem();
                double cantidad = Double.parseDouble(cantidadOrigen.getText());

                double resultado = cambioDivisas.realizarCambio(origen, destino, cantidad);
                if (resultado == 0.0) {
                    JOptionPane.showMessageDialog(this, "No hay tasa disponible entre esas divisas.");
                } else {
                    cantidadDestino.setText(String.format("%.2f", resultado));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese una cantidad válida.");
            }
        });

        centerPanel.add(btnAceptar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnVolver = new JButton("Volver");
        btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVolver.addActionListener(e -> {
            new IngresoCliente(registro, alertasBancarias).setVisible(true); 
            dispose();
        });

        centerPanel.add(btnVolver);
        add(centerPanel, BorderLayout.CENTER);

        // === FOOTER ===
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(220, 214, 207));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel contacto = new JLabel(
                "<html><b>CONTÁCTANOS</b><br>1234565465<br><br><b>UBICACIÓN</b><br>Calle Falsa 123</html>");
        JLabel trabaja = new JLabel(
                "<html><b>TRABAJA CON NOSOTROS</b><br>Puestos disponibles<br><br><b>RECLAMOS O SUGERENCIAS</b></html>");
        JLabel vigilancia = new JLabel("<html><b>VIGILADOS POR</b><br>LOREM IPSUM DOLOR SIT AMET</html>");

        footer.add(contacto);
        footer.add(trabaja);
        footer.add(vigilancia);

        add(footer, BorderLayout.SOUTH);
    }
}

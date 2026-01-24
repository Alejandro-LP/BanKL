package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;

public class RetirarCC extends JFrame {
    private TarjetaCredito tarjeta;
    private AlertasBancarias alertasBancarias;

    public RetirarCC(Registro registro, AlertasBancarias alertasBancarias) {
        this.alertasBancarias = alertasBancarias;

        ClienteNatural cliente = registro.getClienteAutenticado();
        if (cliente != null && cliente.getTarjetaCredito() != null) {
            this.tarjeta = cliente.getTarjetaCredito();
        } else {
            JOptionPane.showMessageDialog(this, "Cliente o tarjeta de crédito no encontrada.");
            dispose();
            return;
        }

        setTitle("Cuenta de crédito");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header con logo
        JLabel logo = new JLabel("<html><div style='font-family:sans-serif;'>"
                + "<span style='font-size:26px;'>"
                + "<b style='color:#001F4E;'>Ban</b><b style='color:#888888;'>KL</b>"
                + "</span><br>"
                + "<span style='font-size:12px;'>Innovación financiera para tu futuro</span></div></html>");
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(logo, BorderLayout.NORTH);

        // Centro
        JPanel centro = new JPanel(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Avance con tarjeta de crédito", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblCupo = new JLabel("Cupo disponible:");
        JLabel montoDisponible = new JLabel(String.format("%.2f $", tarjeta.getCupo()), SwingConstants.CENTER);
        montoDisponible.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblIngreso = new JLabel("Ingrese el valor que desea retirar:", SwingConstants.CENTER);
        JTextField txtMonto = new JTextField();

        JButton btnRetirar = new JButton("Realizar Avance");

        centro.add(title);
        centro.add(lblCupo);
        centro.add(montoDisponible);
        centro.add(lblIngreso);
        centro.add(txtMonto);
        centro.add(btnRetirar);

        panel.add(centro, BorderLayout.CENTER);

        // Footer
        JLabel contacto = new JLabel("<html><center>Contáctanos<br>12345654565</center></html>", JLabel.CENTER);
        contacto.setForeground(Color.GRAY);
        panel.add(contacto, BorderLayout.SOUTH);

        btnRetirar.addActionListener(e -> {
            try {
                double monto = Double.parseDouble(txtMonto.getText());
                if (tarjeta.retirar(monto)) {
                    // REGISTRO DE ALERTA
                    alertasBancarias.registrarAlerta("Avance", "Se hizo un avance de $" + monto + " a su tarjeta de crédito.");

                    JOptionPane.showMessageDialog(this,
                            "Avance exitoso.\nNuevo disponible: $" + tarjeta.getCupo() +
                            "\nDeuda actual: $" + Math.abs(tarjeta.getSaldo()));

                    dispose(); // Cierra la ventana actual
                    new Cuentas(registro, alertasBancarias).setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(this, "Fondos insuficientes.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
            }
        });

        add(panel);
        setVisible(true);
    }
}

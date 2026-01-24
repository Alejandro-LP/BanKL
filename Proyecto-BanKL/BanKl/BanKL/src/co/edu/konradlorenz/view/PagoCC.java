package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;

public class PagoCC extends JFrame {
    private TarjetaCredito tarjeta;
    private AlertasBancarias alertasBancarias;

    public PagoCC(Registro registro, AlertasBancarias alertasBancarias) {
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
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel de contenido
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("Cuenta de crédito");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel deuda = new JLabel("Su deuda de : ");
        deuda.setFont(new Font("Arial", Font.PLAIN, 16));
        deuda.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel montoDeuda = new JLabel(String.format("%.1f$", tarjeta.getSaldo()));
        montoDeuda.setFont(new Font("Arial", Font.BOLD, 20));
        montoDeuda.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruccion = new JLabel("Ingrese el valor que desea pagar:");
        instruccion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField campoPago = new JTextField();
        campoPago.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton botonPagar = new JButton("Pagar");
        botonPagar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel resultado = new JLabel("");
        resultado.setAlignmentX(Component.CENTER_ALIGNMENT);

        botonPagar.addActionListener(e -> {
            try {
                double valor = Double.parseDouble(campoPago.getText());
                tarjeta.consignar(valor);
                
                // REGISTRAR ALERTA DE PAGO
                alertasBancarias.registrarAlerta("Pago", "Se realizó un pago de $" + valor + " a su tarjeta de crédito.");

                resultado.setText("Pago exitoso. Nuevo saldo: $" + tarjeta.getSaldo());
                montoDeuda.setText(String.format("%.1f$", tarjeta.getSaldo()));
            } catch (NumberFormatException ex) {
                resultado.setText("Ingrese un valor válido.");
            }
        });

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(15));
        panel.add(deuda);
        panel.add(montoDeuda);
        panel.add(Box.createVerticalStrut(15));
        panel.add(instruccion);
        panel.add(campoPago);
        panel.add(Box.createVerticalStrut(10));
        panel.add(botonPagar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(resultado);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}

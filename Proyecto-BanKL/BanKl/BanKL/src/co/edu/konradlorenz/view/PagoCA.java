package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;

public class PagoCA extends JFrame {
    private final Registro registro;
    private final AlertasBancarias alertasBancarias;
    private TarjetaDebito tarjeta;
    private JLabel saldoLabel;
    private JTextField valorField;

    public PagoCA(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;
        this.alertasBancarias = alertasBancarias;

        ClienteNatural cliente = registro.getClienteAutenticado();
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente autenticado.");
            dispose();
            return;
        }

        tarjeta = cliente.getCuentas().stream()
                .filter(c -> c instanceof TarjetaDebito)
                .map(c -> (TarjetaDebito) c)
                .findFirst()
                .orElse(null);

        if (tarjeta == null) {
            JOptionPane.showMessageDialog(this, "No se encontró una tarjeta de débito.");
            dispose();
            return;
        }

        setTitle("Consignar a Cuenta de Ahorros");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(221, 214, 208));
        JLabel logo = new JLabel("<html><div style='font-size:24px;'><b style='color:#001F4E;'>Ban</b><b style='color:#FFFFFF;'>KL</b></div><div>Innovación financiera para tu futuro</div></html>");
        logo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        header.add(logo, BorderLayout.WEST);

        JLabel bienvenido = new JLabel("Hola, Bienvenido", SwingConstants.RIGHT);
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> dispose());
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.add(bienvenido, BorderLayout.NORTH);
        derecha.add(salir, BorderLayout.SOUTH);
        header.add(derecha, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Centro
        JPanel centro = new JPanel();
        centro.setBackground(Color.WHITE);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel titulo = new JLabel("Cuentas");
        titulo.setOpaque(true);
        titulo.setBackground(new Color(103, 141, 168));
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setMaximumSize(new Dimension(600, 40));
        centro.add(titulo);

        centro.add(Box.createVerticalStrut(30));

        saldoLabel = new JLabel("Saldo: " + String.format("%.2f", tarjeta.getSaldo()) + "$");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        saldoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(saldoLabel);

        centro.add(Box.createVerticalStrut(15));

        JLabel instruccion = new JLabel("Ingrese el valor que desea consignar a su cuenta de ahorros");
        instruccion.setFont(new Font("Arial", Font.PLAIN, 14));
        instruccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(instruccion);

        centro.add(Box.createVerticalStrut(10));

        valorField = new JTextField();
        valorField.setMaximumSize(new Dimension(200, 30));
        valorField.setHorizontalAlignment(JTextField.CENTER);
        valorField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(valorField);

        centro.add(Box.createVerticalStrut(20));

        JButton aceptarBtn = new JButton("Aceptar");
        aceptarBtn.setPreferredSize(new Dimension(120, 35));
        aceptarBtn.setBackground(new Color(103, 141, 168));
        aceptarBtn.setForeground(Color.WHITE);
        aceptarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        aceptarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        aceptarBtn.addActionListener(e -> realizarConsignacion());
        centro.add(aceptarBtn);

        centro.add(Box.createVerticalStrut(10));

        JButton volverBtn = new JButton("Volver");
        volverBtn.setPreferredSize(new Dimension(120, 30));
        volverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        volverBtn.addActionListener(e -> {
            dispose();
            new Cuentas(registro, alertasBancarias).setVisible(true);
        });
        centro.add(volverBtn);

        add(centro, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(221, 214, 208));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        footer.add(new JLabel("<html><center><b>Contáctanos</b><br>12314565465<br><b>Ubicación</b><br>Calle Falsa 123</center></html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><center><b>Trabaja Con Nosotros</b><br>Puestos disponibles<br><b>Reclamos o Sugerencias</b></center></html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><center><b>Vigilados por</b><br>Lorem ipsum dolor sit amet</center></html>", SwingConstants.CENTER));

        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void realizarConsignacion() {
        try {
            double valor = Double.parseDouble(valorField.getText());
            if (valor <= 0) {
                JOptionPane.showMessageDialog(this, "Ingrese un valor mayor que cero.");
                return;
            }

            tarjeta.consignar(valor);
            saldoLabel.setText("Saldo: " + String.format("%.2f", tarjeta.getSaldo()) + "$");

            //  Registrar alerta de consignación
            alertasBancarias.registrarAlerta("Consignación", "Se consignaron $" + valor + " a la cuenta de ahorros.");

            JOptionPane.showMessageDialog(this, "Consignación realizada con éxito.");
            valorField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.");
        }
    }
}

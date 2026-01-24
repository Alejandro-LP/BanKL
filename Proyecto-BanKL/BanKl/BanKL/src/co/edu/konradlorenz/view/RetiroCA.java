package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RetiroCA extends JFrame {
    private Registro registro;
    private AlertasBancarias alertasBancarias;

    public RetiroCA(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;
        this.alertasBancarias = alertasBancarias;

        setTitle("Retiro - Cuenta de Ahorros");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 215, 210));

        JLabel logo = new JLabel("<html><div style='font-family:sans-serif;'>"
                + "<span style='font-size:30px;'><b style='color:#001F4E;'>Ban</b><b style='color:#FFFFFF;'>KL</b></span><br>"
                + "<span style='font-size:12px;'>Innovación financiera para tu futuro</span></div></html>");
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        header.add(logo, BorderLayout.WEST);

        JLabel bienvenida = new JLabel(
                "<html><div style='font-size:14px; color:#001F4E;'><b>HOLA,</b> BIENVENIDO</div></html>");
        bienvenida.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        header.add(bienvenida, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // CENTRO
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titulo = new JLabel("Cuentas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(107, 138, 163));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setMaximumSize(new Dimension(300, 40));
        titulo.setPreferredSize(new Dimension(300, 40));
        center.add(titulo);

        center.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton tipoCuenta = new JButton("Cuenta de ahorros");
        tipoCuenta.setEnabled(false);
        tipoCuenta.setBackground(new Color(107, 138, 163));
        tipoCuenta.setForeground(Color.WHITE);
        tipoCuenta.setAlignmentX(Component.CENTER_ALIGNMENT);
        tipoCuenta.setMaximumSize(new Dimension(200, 40));
        center.add(tipoCuenta);

        center.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel cantidadLabel = new JLabel("Ingrese la cantidad de dinero que desea retirar:");
        cantidadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(cantidadLabel);

        JTextField cantidadField = new JTextField();
        cantidadField.setMaximumSize(new Dimension(200, 30));
        cantidadField.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(cantidadField);

        center.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton retirarBtn = new JButton("Retirar");
        retirarBtn.setBackground(new Color(107, 138, 163));
        retirarBtn.setForeground(Color.WHITE);
        retirarBtn.setFocusPainted(false);
        retirarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        retirarBtn.setMaximumSize(new Dimension(150, 40));
        center.add(retirarBtn);

        center.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton volverBtn = new JButton("Volver a cuentas");
        volverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        volverBtn.addActionListener(e -> {
            dispose();
            new Cuentas(registro, alertasBancarias).setVisible(true);
        });
        center.add(volverBtn);

        add(center, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(220, 215, 210));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel contacto = new JLabel(
                "<html><b>Contáctanos</b><br>12345654565<br><br><b>Ubicación</b><br>Calle Falsa 123</html>");
        JLabel trabaja = new JLabel(
                "<html><center><b>Trabaja Con Nosotros</b><br>Puestos disponibles<br><br>Reclamos o Sugerencias</center></html>",
                SwingConstants.CENTER);
        JLabel vigilados = new JLabel(
                "<html><center><b>Vigilados por</b><br>Lorem Ipsum Dolor Sit Amet</center></html>",
                SwingConstants.CENTER);

        footer.add(contacto);
        footer.add(trabaja);
        footer.add(vigilados);

        add(footer, BorderLayout.SOUTH);

        // LÓGICA DE RETIRO
        retirarBtn.addActionListener(e -> {
            String texto = cantidadField.getText();
            try {
                double monto = Double.parseDouble(texto);
                ClienteNatural cliente = registro.getClienteAutenticado();

                if (cliente != null) {
                    List<Cuenta> cuentas = cliente.getCuentas();
                    for (Cuenta cuenta : cuentas) {
                        if (cuenta instanceof TarjetaDebito debito) {
                            boolean exito = debito.retirar(monto);
                            if (exito) {
                                //  Registrar alerta de retiro
                                alertasBancarias.registrarAlerta("Retiro", "Se retiraron $" + monto + " de la cuenta de ahorros.");

                                JOptionPane.showMessageDialog(this,
                                        "Retiro exitoso. Nuevo saldo: $" + debito.getSaldo());
                            } else {
                                JOptionPane.showMessageDialog(this, "Fondos insuficientes o monto inválido.", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            return;
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.");
            }
        });

        setVisible(true);
    }
}

package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class Cuentas extends JFrame {
    private final Registro registro;
    private final AlertasBancarias alertasBancarias;

    public Cuentas(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;
        this.alertasBancarias = alertasBancarias;

        setTitle("Cuentas del Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 215, 210));

        JLabel logo = new JLabel("<html><div style='font-family:sans-serif;'>"
                + "<span style='font-size:30px;'>"
                + "<b style='color:#001F4E;'>Ban</b><b style='color:#FFFFFF;'>KL</b>"
                + "</span><br>"
                + "<span style='font-size:12px;'>Innovación financiera para tu futuro</span></div></html>");
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        header.add(logo, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(Color.WHITE);
        centro.setBorder(new EmptyBorder(20, 20, 20, 20));

        ClienteNatural cliente = registro.getClienteAutenticado();

        if (cliente != null && cliente.getCuentas() != null) {
            List<Cuenta> cuentas = cliente.getCuentas();
            for (Cuenta cuenta : cuentas) {
                if (cuenta instanceof TarjetaDebito debito) {
                    centro.add(crearPanelCuenta(
                            "Cuenta de Ahorros",
                            debito.getNumeroTarjeta(),
                            String.format("%.2f", debito.getSaldo()),
                            "",
                            debito.getFechaExpiracion(),
                            "Ingresar",
                            "Retirar",
                            debito));
                } else if (cuenta instanceof TarjetaCredito credito) {
                    centro.add(crearPanelCuenta(
                            "Tarjeta de Crédito",
                            credito.getNumeroTarjeta(),
                            String.format("%.2f", Math.abs(credito.getSaldo())),
                            String.format("%.2f", credito.disponible()),
                            credito.getFechaExpiracion(),
                            "Pagar",
                            "Avance",
                            credito));
                }
                centro.add(Box.createVerticalStrut(20));
            }
        } else {
            JLabel error = new JLabel("No hay cliente autenticado o no tiene cuentas.");
            error.setForeground(Color.RED);
            centro.add(error);
        }

        JScrollPane scrollPane = new JScrollPane(centro);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        JButton volverBtn = new JButton("Volver");

        volverBtn.addActionListener(e -> {
            dispose();
            new IngresoCliente(registro, alertasBancarias).setVisible(true);
        });

        footer.add(volverBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel crearPanelCuenta(String tipoCuenta, String numeroTarjeta, String saldoODuuda, String disponible,
                                    String vencimiento, String btn1, String btn2, Cuenta cuenta) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setBackground(new Color(245, 245, 245));
        panel.setMaximumSize(new Dimension(700, 180));

        JLabel tituloLabel = new JLabel(tipoCuenta, SwingConstants.CENTER);
        tituloLabel.setOpaque(true);
        tituloLabel.setBackground(new Color(65, 119, 197));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setPreferredSize(new Dimension(panel.getWidth(), 30));
        panel.add(tituloLabel, BorderLayout.NORTH);

        String info = "<html><div style='padding:10px;'>"
                + "<b>Número:</b> " + numeroTarjeta + "<br>"
                + (tipoCuenta.contains("Crédito") ? "<b>Deuda:</b> $" : "<b>Saldo:</b> $") + saldoODuuda + "<br>";

        if (!disponible.isEmpty()) {
            info += "<b>Disponible:</b> $" + disponible + "<br>";
        }

        info += "<b>Vencimiento:</b> " + vencimiento + "</div></html>";

        JLabel datosLabel = new JLabel(info);
        datosLabel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.add(datosLabel, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton b1 = new JButton(btn1);
        JButton b2 = new JButton(btn2);
        JButton cambiarTarjetaBtn = new JButton("Cambiar Tarjeta");

        b1.addActionListener(e -> {
            if (btn1.equals("Ingresar")) {
                new PagoCA(registro, alertasBancarias).setVisible(true);
            } else if (btn1.equals("Pagar")) {
                new PagoCC(registro, alertasBancarias).setVisible(true);
            }
        });

        b2.addActionListener(e -> {
            if (btn2.equals("Retirar")) {
                new RetiroCA(registro, alertasBancarias).setVisible(true);
            } else if (btn2.equals("Avance")) {
                new RetirarCC(registro, alertasBancarias).setVisible(true);
            }
        });

        cambiarTarjetaBtn.addActionListener(e -> {
            ClienteNatural cliente = registro.getClienteAutenticado();
            if (cliente != null) {
                new CambioTarjeta(registro, cliente.getId(), alertasBancarias).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No hay cliente autenticado.");
            }
        });

        botones.add(b1);
        botones.add(b2);
        botones.add(cambiarTarjetaBtn);

        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }
}

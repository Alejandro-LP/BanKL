package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;

public class CambioTarjeta extends JFrame {

    private final String clienteId;
    private final AlertasBancarias alertasBancarias;

    public CambioTarjeta(Registro registro, String clienteId, AlertasBancarias alertasBancarias) {
        this.clienteId = clienteId;
        this.alertasBancarias = alertasBancarias;

        setTitle("Cambio de Tarjeta");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 215, 210));

        JLabel logo = new JLabel("<html><div style='font-family:sans-serif;'>"
                + "<span style='font-size:30px;'>"
                + "<b style='color:#001F4E;'>Ban</b><b style='color:#FFFFFF;'>KL</b>"
                + "</span><br>"
                + "<span style='font-size:12px;'>Innovación financiera para tu futuro</span></div></html>");
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        header.add(logo, BorderLayout.WEST);

        JLabel bienvenida = new JLabel("<html><b>HOLA,<br>BIENVENIDO</b></html>", SwingConstants.RIGHT);
        bienvenida.setFont(new Font("Serif", Font.BOLD, 14));
        bienvenida.setBorder(new EmptyBorder(10, 10, 10, 20));
        header.add(bienvenida, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // === PANEL CENTRAL ===
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(new EmptyBorder(20, 20, 20, 20));
        centro.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Cambio de Tarjeta", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(0x4177C5));
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setPreferredSize(new Dimension(centro.getWidth(), 40));
        centro.add(titulo, BorderLayout.NORTH);

        JPanel tarjetaPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        tarjetaPanel.setBackground(Color.WHITE);
        tarjetaPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        tarjetaPanel.setPreferredSize(new Dimension(400, 200));

        JTextField documentoField = new JTextField(clienteId);
        documentoField.setEditable(false);
        JLabel documentoLabel = new JLabel("Documento del cliente:");
        JLabel infoLabel = new JLabel("Seleccione el tipo de tarjeta a cambiar:", SwingConstants.CENTER);
        JComboBox<String> tipoTarjetaCombo = new JComboBox<>(new String[]{"Débito", "Crédito"});
        JButton generarBtn = new JButton("Generar nueva tarjeta");

        JTextArea resultadoArea = new JTextArea(5, 30);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultadoArea.setLineWrap(true);
        resultadoArea.setWrapStyleWord(true);

        tarjetaPanel.add(documentoLabel);
        tarjetaPanel.add(documentoField);
        tarjetaPanel.add(infoLabel);
        tarjetaPanel.add(tipoTarjetaCombo);
        tarjetaPanel.add(generarBtn);
        tarjetaPanel.add(new JScrollPane(resultadoArea));

        centro.add(tarjetaPanel, BorderLayout.CENTER);

        // === BOTÓN VOLVER ===
        JButton volverBtn = new JButton("Volver");
        volverBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        volverBtn.setBackground(new Color(200, 200, 200));

        volverBtn.addActionListener(e -> {
            new Cuentas(registro, alertasBancarias).setVisible(true);
            dispose();
        });

        JPanel volverPanel = new JPanel();
        volverPanel.setBackground(Color.WHITE);
        volverPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        volverPanel.add(volverBtn);
        centro.add(volverPanel, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        // === FOOTER ===
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(230, 220, 210));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));

        footer.add(new JLabel("<html><b>CONTACTANOS</b><br>1234565465</html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><b>TRABAJA CON NOSOTROS</b><br>Puestos disponibles</html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><b>VIGILADOS POR</b><br>LOREM IPSUM DOLOR SIT AMET</html>", SwingConstants.CENTER));

        add(footer, BorderLayout.SOUTH);

        // === LÓGICA DEL BOTÓN GENERAR TARJETA ===
        generarBtn.addActionListener(e -> {
            String documento = documentoField.getText().trim();
            if (documento.isEmpty()) {
                resultadoArea.setText("⚠️ Por favor ingrese un documento.");
                return;
            }

            ClienteNatural cliente = registro.buscarClientePorDocumento(documento);
            if (cliente == null) {
                resultadoArea.setText("❌ Cliente no encontrado.");
                return;
            }

            String tipo = (String) tipoTarjetaCombo.getSelectedItem();
            EmisionTarjeta emision = new EmisionTarjeta(new SecureRandom(), DateTimeFormatter.ofPattern("MM/yy"));
            Cuenta cuentaModificada = null;

            for (Cuenta cuenta : cliente.getCuentas()) {
                if (tipo.equals("Débito") && cuenta instanceof TarjetaDebito) {
                    cuentaModificada = cuenta;
                    break;
                } else if (tipo.equals("Crédito") && cuenta instanceof TarjetaCredito) {
                    cuentaModificada = cuenta;
                    break;
                }
            }

            if (cuentaModificada != null) {
                String nuevaTarjeta = emision.generarNumeroTarjeta();
                int nuevoCVV = emision.generarCVV();
                String nuevaFecha = emision.asignarFechaExpiracion();

                cuentaModificada.setNumeroTarjeta(nuevaTarjeta);
                cuentaModificada.setCvv(nuevoCVV);
                cuentaModificada.setFechaExpiracion(nuevaFecha);

                resultadoArea.setText("✅ Nueva tarjeta generada:\n"
                        + "Últimos 4 dígitos: " + nuevaTarjeta.substring(12) + "\n"
                        + "CVV: " + nuevoCVV + "\n"
                        + "Válida hasta: " + nuevaFecha);
            } else {
                resultadoArea.setText("❌ No se encontró una cuenta de tipo " + tipo + ".");
            }
        });
    }
}

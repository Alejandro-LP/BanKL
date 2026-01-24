package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;

public class IngresoCliente extends JFrame {

    private Registro registro;
    private AlertasBancarias alertasBancarias;

    public IngresoCliente(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro;
        this.alertasBancarias = alertasBancarias;

        setTitle("Panel del Cliente - BanKL");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === HEADER ===
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 214, 207));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel logoLabel = new JLabel(
                "<html><span style='font-size:28px; font-weight:bold; color:#001f4d;'>Ban</span><span style='font-size:28px; font-weight:bold;color:#ffffff'>KL</span><br>Innovación financiera para tu futuro</html>");
        header.add(logoLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("<html><div style='text-align:right;'>Hola,<br>Bienvenido</div></html>");
        JButton logoutButton = new JButton("Salir");

        logoutButton.addActionListener(e -> {
            dispose();
            new HomePage(registro).setVisible(true);
        });

        rightPanel.add(welcomeLabel, BorderLayout.CENTER);
        rightPanel.add(logoutButton, BorderLayout.EAST);
        header.add(rightPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // === CENTRO ===
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botones: Cuentas, Divisas, Alertas
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        JButton cuentasBtn = new JButton("Cuentas");
        JButton divisasBtn = new JButton("Divisas");
        JButton alertasBtn = new JButton("Alertas");

        // Estilo botones
        estilizarBoton(cuentasBtn);
        estilizarBoton(divisasBtn);
        estilizarBoton(alertasBtn);

        cuentasBtn.addActionListener(e -> {
            new Cuentas(registro,alertasBancarias).setVisible(true);
            dispose();
        });

        divisasBtn.addActionListener(e -> {
            new CambioDeDivisasV(registro,alertasBancarias).setVisible(true);
            dispose();
        });

        alertasBtn.addActionListener(e -> {
            new AlertasV(alertasBancarias, registro).setVisible(true);
            dispose();
        });

        buttonPanel.add(cuentasBtn);
        buttonPanel.add(divisasBtn);
        buttonPanel.add(alertasBtn);

        // Imagen decorativa centrada
        JPanel imagenPanel = new JPanel(new BorderLayout());
        imagenPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        imagenPanel.setPreferredSize(new Dimension(500, 200));

        JLabel imageLabel;
        try {
            ImageIcon iconoOriginal = new ImageIcon("E:/Programacion/tecnicasDeProgramacion/BanKL/Proyecto-BanKL/Imagenes proyecto/app.png");
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(500, 200, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(imagenEscalada), SwingConstants.CENTER);
        } catch (Exception ex) {
            imageLabel = new JLabel("Imagen", SwingConstants.CENTER);
        }
        imagenPanel.add(imageLabel, BorderLayout.CENTER);

        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(imagenPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // === FOOTER ===
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(220, 214, 207));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        footer.add(
                new JLabel("<html><b>CONTÁCTANOS</b><br>1234565465<br><br><b>UBICACIÓN</b><br>Calle Falsa 123</html>"));
        footer.add(new JLabel(
                "<html><b>TRABAJA CON NOSOTROS</b><br>Puestos disponibles<br><br><b>RECLAMOS O SUGERENCIAS</b></html>"));
        footer.add(new JLabel("<html><b>VIGILADOS POR</b><br>LOREM IPSUM DOLOR SIT AMET</html>"));

        add(footer, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton boton) {
        boton.setPreferredSize(new Dimension(120, 40));
        boton.setBackground(new Color(108, 160, 220));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
    }
}

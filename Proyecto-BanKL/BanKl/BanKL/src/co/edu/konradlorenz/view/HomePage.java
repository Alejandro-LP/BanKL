package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HomePage extends JFrame {
    private Registro registro;
    private AlertasBancarias alertasBancarias;

    // Constructor por defecto
    public HomePage() {
        this(new Registro(), new AlertasBancarias());
    }

    // Constructor con Registro existente
    public HomePage(Registro registro) {
        this(registro, new AlertasBancarias());
    }

    // Constructor con Registro y AlertasBancarias
    public HomePage(Registro registro, AlertasBancarias alertasBancarias) {
        this.registro = registro != null ? registro : new Registro();
        this.alertasBancarias = alertasBancarias != null ? alertasBancarias : new AlertasBancarias();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("BanKL - Inicio");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 215, 210));
        JLabel logo = new JLabel("<html><div style='font-family:sans-serif;'>"
                + "<span style='font-size:30px;'>"
                + "<b style='color:#001F4E;'>Ban</b><b style='color:#FFFFFF;'>KL</b>"
                + "</span><br>"
                + "<span style='font-size:12px;'>Innovación financiera para tu futuro</span></div></html>");
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton loginButton = new JButton("Ingresa");
        JButton registerButton = new JButton("Regístrate");
        buttons.add(loginButton);
        buttons.add(registerButton);

        header.add(logo, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);

        // CENTRO
        JPanel center = new JPanel(new GridLayout(1, 3, 20, 0));
        center.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        center.add(createCard("¿Quiénes somos?",
            "BanKL es una entidad financiera innovadora que trabaja por el bienestar económico de sus clientes. "
          + "Ofrecemos servicios modernos y seguros. "));
        center.add(createCard("Nuestros productos",
            "Tarjetas de débito y crédito, ingreso y retiro de dinero, cambio de divisa, etc."
          + "Descubre todo lo que podemos hacer por tu futuro financiero."));
        center.add(createCard("Beneficios",
            "Accede a alertas bancarias en tiempo real, y herramientas digitales "
          + "que te ayudan a tomar mejores decisiones con tu dinero."));

        // FOOTER
        JPanel footer = new JPanel(new GridLayout(1, 3));
        footer.setBackground(new Color(220, 215, 210));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footer.add(new JLabel("<html><b>Contáctanos</b><br>1234565465<br><br><b>Ubicación</b><br>Calle Falsa 123</html>"));
        footer.add(new JLabel("<html><center><b>Trabaja Con Nosotros</b><br><br><br></center></html>", SwingConstants.CENTER));
        footer.add(new JLabel("<html><center><b>Vigilados por</b><br> SFC </center></html>", SwingConstants.RIGHT));

        // AGREGAR A FRAME
        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // LISTENERS
        registerButton.addActionListener((ActionEvent e) -> {
            dispose();
            new RegistroV(registro, alertasBancarias).setVisible(true); 
        });

        loginButton.addActionListener((ActionEvent e) -> {
            dispose();
            new IngresoV(registro, alertasBancarias).setVisible(true);
        });
    }

    private JPanel createCard(String title, String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(97, 139, 180));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        return panel;
    }
}

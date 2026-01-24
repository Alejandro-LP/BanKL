package co.edu.konradlorenz.view;

import co.edu.konradlorenz.model.Alerta;
import co.edu.konradlorenz.model.AlertasBancarias;
import co.edu.konradlorenz.model.Registro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AlertasV extends JFrame {

    private final AlertasBancarias alertasBancarias;
    private final Registro registro;

    public AlertasV(AlertasBancarias alertasBancarias, Registro registro) {
        this.alertasBancarias = alertasBancarias;
        this.registro = registro;

        setTitle("Alertas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 214, 208));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel logo = new JLabel(
                "<html><span style='font-size:36px; font-family:serif; font-weight:bold; color:#0F245E;'>Ban<span style='color:#FFFFFF;'>KL</span></span></html>");
        JLabel slogan = new JLabel(
                "<html><div style='font-size:14px;'>Innovación financiera para tu futuro</div></html>");
        JPanel logoPanel = new JPanel(new GridLayout(2, 1));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.add(slogan);

        JLabel bienvenida = new JLabel(
                "<html><div style='font-size:14px; color:#0F245E;'>HOLA,<br>BIENVENIDO</div></html>",
                SwingConstants.RIGHT);
        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> System.exit(0));

        JPanel usuarioPanel = new JPanel(new BorderLayout());
        usuarioPanel.setOpaque(false);
        usuarioPanel.add(bienvenida, BorderLayout.CENTER);
        usuarioPanel.add(salir, BorderLayout.SOUTH);

        header.add(logoPanel, BorderLayout.WEST);
        header.add(usuarioPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== CENTRO =====
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Alertas", SwingConstants.CENTER);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(101, 142, 173));
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        titulo.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

        JLabel subtitulo = new JLabel("Lista de alertas", SwingConstants.CENTER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JTextArea areaAlertas = new JTextArea();
        areaAlertas.setEditable(false);
        areaAlertas.setFont(new Font("Monospaced", Font.PLAIN, 14));

        List<Alerta> alertas = alertasBancarias.revisarAlertas();
        if (alertas.isEmpty()) {
            areaAlertas.setText("Lista vacía.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Alerta alerta : alertas) {
                sb.append("ID: ").append(alerta.getId())
                        .append(" | Tipo: ").append(alerta.getTipo())
                        .append(" | Desc: ").append(alerta.getDescripcion()).append("\n");
            }
            areaAlertas.setText(sb.toString());
        }

        JScrollPane scrollPane = new JScrollPane(areaAlertas);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JButton volver = new JButton("Volver");
        volver.setAlignmentX(Component.CENTER_ALIGNMENT);
        volver.addActionListener((ActionEvent e) -> {
            dispose();
            new IngresoCliente(registro, alertasBancarias).setVisible(true); 
        });

        centro.add(titulo);
        centro.add(Box.createVerticalStrut(10));
        centro.add(subtitulo);
        centro.add(scrollPane);
        centro.add(Box.createVerticalStrut(15));
        centro.add(volver);

        add(centro, BorderLayout.CENTER);

        // ===== PIE DE PÁGINA =====
        JPanel pie = new JPanel(new GridLayout(1, 3));
        pie.setBackground(new Color(220, 214, 208));
        pie.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel contacto = new JLabel(
                "<html><b>Contáctanos</b><br>1234565465<br><br><b>Ubicación</b><br>Calle Falsa 123</html>");
        JLabel trabaja = new JLabel(
                "<html><b>Trabaja Con Nosotros</b><br>Puestos disponibles<br><br><b>Reclamos o Sugerencias</b></html>");
        JLabel legal = new JLabel("<html><b>Vigilados por</b><br>LOREM IPSUM DOLOR SIT AMET</html>");

        pie.add(contacto);
        pie.add(trabaja);
        pie.add(legal);

        add(pie, BorderLayout.SOUTH);
        setVisible(true);

    }
}
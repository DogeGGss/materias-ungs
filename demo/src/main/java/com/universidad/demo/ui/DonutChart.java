package com.universidad.demo.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

/** Anillo de progreso simple: porcentaje en el centro y una etiqueta debajo. */
public class DonutChart extends JComponent {

    private static final int DIAMETRO = 130;
    private static final int GROSOR = 14;

    private final int porcentaje;
    private final Color color;
    private final String etiqueta;

    public DonutChart(int porcentaje, Color color, String etiqueta) {
        this.porcentaje = Math.max(0, Math.min(100, porcentaje));
        this.color = color;
        this.etiqueta = etiqueta;
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(DIAMETRO + 20, DIAMETRO + 46));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (getWidth() - DIAMETRO) / 2;
        int y = 4;

        g2.setStroke(new BasicStroke(GROSOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Tema.BORDE);
        g2.draw(new Arc2D.Double(x, y, DIAMETRO, DIAMETRO, 90, 360, Arc2D.OPEN));

        if (porcentaje > 0) {
            g2.setColor(color);
            double angulo = 360.0 * porcentaje / 100.0;
            g2.draw(new Arc2D.Double(x, y, DIAMETRO, DIAMETRO, 90, -angulo, Arc2D.OPEN));
        }

        g2.setFont(new Font(Tema.FUENTE, Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        String texto = porcentaje + "%";
        g2.setColor(Tema.TEXTO);
        g2.drawString(texto, x + (DIAMETRO - fm.stringWidth(texto)) / 2f, y + DIAMETRO / 2f + fm.getAscent() / 2f - 4);

        g2.setFont(new Font(Tema.FUENTE, Font.BOLD, 13));
        fm = g2.getFontMetrics();
        g2.setColor(color);
        g2.drawString(etiqueta, (getWidth() - fm.stringWidth(etiqueta)) / 2f, y + DIAMETRO + 26);

        g2.dispose();
    }
}

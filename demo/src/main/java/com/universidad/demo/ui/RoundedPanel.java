package com.universidad.demo.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/** JPanel con esquinas redondeadas y un borde de contorno opcional. */
public class RoundedPanel extends JPanel {

    private final int radio;
    private Color colorBorde;

    public RoundedPanel(LayoutManager layout, int radio) {
        super(layout);
        this.radio = radio;
        setOpaque(false);
    }

    public void setColorBorde(Color colorBorde) {
        this.colorBorde = colorBorde;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radio, radio));
        if (colorBorde != null) {
            g2.setColor(colorBorde);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1.5f, getHeight() - 1.5f, radio, radio));
        }
        g2.dispose();
        super.paintComponent(g);
    }
}

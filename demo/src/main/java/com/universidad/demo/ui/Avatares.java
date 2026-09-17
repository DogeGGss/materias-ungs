package com.universidad.demo.ui;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/** Carga los avatares de usuario desde los recursos estáticos y los recorta en forma circular. */
public final class Avatares {

    private Avatares() {}

    public static ImageIcon cargar(Class<?> origen, String avatarUrl, int tamano) {
        String ruta;
        if (avatarUrl.startsWith("/static/")) {
            ruta = avatarUrl;
        } else if (avatarUrl.startsWith("/")) {
            ruta = "/static" + avatarUrl;
        } else {
            ruta = "/static/" + avatarUrl;
        }

        try (InputStream in = origen.getResourceAsStream(ruta)) {
            if (in == null) {
                return null;
            }
            BufferedImage original = ImageIO.read(in);
            if (original == null) {
                return null;
            }
            Image escalada = original.getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
            return new ImageIcon(recortarComoCirculo(escalada, tamano));
        } catch (Exception e) {
            return null;
        }
    }

    private static BufferedImage recortarComoCirculo(Image imagen, int tamano) {
        BufferedImage circular = new BufferedImage(tamano, tamano, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circular.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, tamano, tamano));
        g2.drawImage(imagen, 0, 0, tamano, tamano, null);
        g2.dispose();
        return circular;
    }
}

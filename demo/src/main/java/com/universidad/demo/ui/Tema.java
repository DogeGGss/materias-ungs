package com.universidad.demo.ui;

import java.awt.Color;

/** Paleta de colores y constantes visuales compartidas por toda la interfaz. */
public final class Tema {

    private Tema() {}

    public static final String FUENTE = "Segoe UI";

    public static final Color FONDO = new Color(0xF3F5F9);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color BORDE = new Color(0xE2E5EC);

    public static final Color TEXTO = new Color(0x1F2430);
    public static final Color TEXTO_MUTED = new Color(0x6B7280);

    // Tecnicatura Universitaria en Informática
    public static final Color TECNICATURA = new Color(0x3B6FE0);
    public static final Color TECNICATURA_CLARO = new Color(0xE8EEFD);

    // Licenciatura en Sistemas
    public static final Color LICENCIATURA = new Color(0x0E9F5C);
    public static final Color LICENCIATURA_CLARO = new Color(0xE1F6EB);

    public static final Color PELIGRO = new Color(0xE5484D);

    public static final int RADIO_TARJETA = 16;
    public static final int RADIO_CHICO = 10;
}

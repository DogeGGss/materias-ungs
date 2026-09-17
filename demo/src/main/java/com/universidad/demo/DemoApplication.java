package com.universidad.demo;

import com.formdev.flatlaf.FlatLightLaf;
import com.universidad.demo.models.Materia;
import com.universidad.demo.models.Usuario;
import com.universidad.demo.services.MateriaService;
import com.universidad.demo.ui.Avatares;
import com.universidad.demo.ui.DonutChart;
import com.universidad.demo.ui.RoundedPanel;
import com.universidad.demo.ui.Tema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DemoApplication {

    // Totales de materias por carrera según los planes de estudio
    private static final int TOTAL_MATERIAS_TECNICATURA = 20;
    private static final int TOTAL_MATERIAS_LICENCIATURA = 37;

    // Materias de Tecnicatura (20 materias) - según el plan de estudios vigente
    // Actualizado según Resolución (CS) UNGS N° 9794/25 (18/12/2025), que deroga la
    // Res. 7673/20 y saca TLED e INGL3 de la Tecnicatura: pasan a ser exclusivas de Licenciatura.
    // PPS1 (Proyecto Profesional I / "Laboratorio de Construcción de Software" en Tecnicatura)
    // sí es compartida entre las dos carreras, aunque con correlativas distintas
    // (ver Materia.correlativasTecnicatura y PlanEstudiosLoader).
    // NOTA: Todas las materias de Tecnicatura están también en Licenciatura
    // Las materias exclusivas de Licenciatura son: ALG, CALC, TCOM, PYE, ING2, ORG2, PPS2, BD2, SOR2, PPS, MOD, ISOC, TTES, GPRO, LABI, TLED, INGL3
    private static final List<String> MATERIAS_TECNICATURA = List.of(
        "TIC", "TIO", "TIO-MAT", "IPROG", "IMAT", "PROG1", "ORG1",
        "PROG2", "SOR1", "LYTN", "PROG3", "PSC", "BD1", "MATD", "EVS",
        "ING1", "TUTIL", "INGL1", "INGL2", "PPS1"
        // LABI, TLED e INGL3 NO están en Tecnicatura, solo en Licenciatura
    );

    // Método para determinar a qué carrera pertenece una materia
    private static String obtenerCarreraMateria(String codigo) {
        // LABI es exclusivo de Licenciatura
        if ("LABI".equals(codigo)) {
            return "LICENCIATURA";
        }

        boolean esTecnicatura = MATERIAS_TECNICATURA.contains(codigo);
        // Todas las materias de Tecnicatura están también en Licenciatura
        if (esTecnicatura) {
            return "AMBAS"; // Pertenece a ambas carreras
        } else {
            return "LICENCIATURA"; // Solo Licenciatura
        }
    }

    private static ConfigurableApplicationContext applicationContext;
    private static JFrame mainFrame;
    private static MateriaService materiaService;
    private static Map<String, Usuario> usuarios;
    private static Usuario usuarioActual;

    public static void main(String[] args) {
        // Deshabilitar modo headless para permitir interfaz gráfica
        System.setProperty("java.awt.headless", "false");

        // Iniciar Spring Boot
        applicationContext = SpringApplication.run(DemoApplication.class, args);

        // Obtener servicios
        materiaService = applicationContext.getBean(MateriaService.class);

        // Inicializar usuarios
        usuarios = Map.of(
            "admin", new Usuario("admin", "Administrador", "/img/admin.jpg", "ADMIN",
                    List.of("Gestionar usuarios", "Configurar sistema")),
            "Pedro", new Usuario("Pedro", "Pedro", "/img/Pedro.jpg", "ESTUDIANTE",
                    List.of("Programación", "Matemáticas")),
            "Valen", new Usuario("Valen", "Valen", "/img/Valen.jpg", "ESTUDIANTE",
                    List.of("Biología", "Química")),
            "Kevin", new Usuario("Kevin", "Kevin", "/img/Kevin.jpg", "ESTUDIANTE",
                    List.of("Biología", "Química"))
        );

        // Verificar que el sistema soporte GUI
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Error: El sistema no soporta interfaz gráfica (modo headless)");
            System.exit(1);
        }

        // Iniciar interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
                UIManager.put("Component.accentColor", Tema.TECNICATURA);
                UIManager.put("Button.arc", 14);
                UIManager.put("Component.arc", 12);
                UIManager.put("ProgressBar.arc", 12);
                UIManager.put("TextComponent.arc", 10);
                UIManager.put("ScrollBar.thumbArc", 999);
                UIManager.put("ScrollBar.width", 12);
                UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
                UIManager.put("defaultFont", new Font(Tema.FUENTE, Font.PLAIN, 13));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mostrarPantallaInicio();
        });
    }

    private static void mostrarPantallaInicio() {
        // Si ya existe un frame, limpiarlo y reutilizarlo
        if (mainFrame != null) {
            mainFrame.getContentPane().removeAll();
            mainFrame.dispose();
        }

        mainFrame = new JFrame("Sistema Universitario - Selección de Perfil");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(Tema.FONDO);

        // Título
        JLabel titulo = new JLabel("Selecciona un perfil para comenzar", SwingConstants.CENTER);
        titulo.setFont(new Font(Tema.FUENTE, Font.BOLD, 26));
        titulo.setForeground(Tema.TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panel.add(titulo, BorderLayout.NORTH);

        // Panel de usuarios
        JPanel usuariosPanel = new JPanel(new GridLayout(2, 2, 24, 24));
        usuariosPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        usuariosPanel.setBackground(Tema.FONDO);

        for (Usuario usuario : usuarios.values()) {
            JButton botonUsuario = crearBotonUsuario(usuario);
            usuariosPanel.add(botonUsuario);
        }

        panel.add(usuariosPanel, BorderLayout.CENTER);
        mainFrame.add(panel);
        mainFrame.setVisible(true);
    }

    private static JButton crearBotonUsuario(Usuario usuario) {
        JButton boton = new JButton();
        boton.setLayout(new BorderLayout());
        boton.setPreferredSize(new Dimension(250, 300));
        boton.setBackground(Tema.SUPERFICIE);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFocusPainted(false);

        ImageIcon imagenIcon = Avatares.cargar(DemoApplication.class, usuario.getAvatarUrl(), 130);

        JLabel imagenLabel = new JLabel();
        if (imagenIcon != null) {
            imagenLabel.setIcon(imagenIcon);
        } else {
            // Si no hay imagen, mostrar un icono por defecto
            imagenLabel.setText("👤");
            imagenLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 60));
            imagenLabel.setForeground(Tema.TEXTO_MUTED);
        }
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Nombre
        JLabel nombreLabel = new JLabel(usuario.getNombreCompleto(), SwingConstants.CENTER);
        nombreLabel.setFont(new Font(Tema.FUENTE, Font.BOLD, 18));
        nombreLabel.setForeground(Tema.TEXTO);

        // Rol
        JLabel rolLabel = new JLabel(usuario.getRol(), SwingConstants.CENTER);
        rolLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        rolLabel.setForeground(Tema.TEXTO_MUTED);
        rolLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(nombreLabel, BorderLayout.CENTER);
        infoPanel.add(rolLabel, BorderLayout.SOUTH);

        JPanel contenidoPanel = new JPanel(new BorderLayout());
        contenidoPanel.setOpaque(false);
        // El padding va en este panel interno, no en el botón: si le ponemos un
        // borde propio al JButton, FlatLaf deja de dibujarle las esquinas redondeadas.
        contenidoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        contenidoPanel.add(imagenLabel, BorderLayout.NORTH);
        contenidoPanel.add(infoPanel, BorderLayout.CENTER);

        boton.add(contenidoPanel, BorderLayout.CENTER);
        boton.addActionListener(e -> {
            usuarioActual = usuario;
            mostrarDashboard();
        });

        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(Tema.TECNICATURA_CLARO);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(Tema.SUPERFICIE);
            }
        });

        return boton;
    }

    private static void mostrarDashboard() {
        mainFrame.getContentPane().removeAll();
        mainFrame.setTitle("Dashboard - " + usuarioActual.getNombreCompleto());
        mainFrame.setSize(1400, 900);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Tema.FONDO);

        // Header
        RoundedPanel headerPanel = new RoundedPanel(new BorderLayout(), Tema.RADIO_TARJETA);
        headerPanel.setBackground(Tema.TECNICATURA);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        ImageIcon imagenIcon = Avatares.cargar(DemoApplication.class, usuarioActual.getAvatarUrl(), 64);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);

        // Imagen del usuario en el header
        JLabel imagenHeaderLabel = new JLabel();
        if (imagenIcon != null) {
            imagenHeaderLabel.setIcon(imagenIcon);
        } else {
            imagenHeaderLabel.setText("👤");
            imagenHeaderLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 42));
            imagenHeaderLabel.setForeground(Color.WHITE);
        }

        JPanel textoPanel = new JPanel(new BorderLayout());
        textoPanel.setOpaque(false);
        JLabel nombreLabel = new JLabel(usuarioActual.getNombreCompleto());
        nombreLabel.setFont(new Font(Tema.FUENTE, Font.BOLD, 20));
        nombreLabel.setForeground(Color.WHITE);
        JLabel rolLabel = new JLabel(usuarioActual.getRol());
        rolLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        rolLabel.setForeground(new Color(255, 255, 255, 200));
        textoPanel.add(nombreLabel, BorderLayout.NORTH);
        textoPanel.add(rolLabel, BorderLayout.SOUTH);

        infoPanel.add(imagenHeaderLabel);
        infoPanel.add(textoPanel);

        JButton cambiarPerfilBtn = new JButton("Cambiar perfil");
        cambiarPerfilBtn.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        cambiarPerfilBtn.setBackground(Color.WHITE);
        cambiarPerfilBtn.setForeground(Tema.TECNICATURA);
        cambiarPerfilBtn.setMargin(new Insets(9, 18, 9, 18));
        cambiarPerfilBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cambiarPerfilBtn.setFocusPainted(false);
        cambiarPerfilBtn.putClientProperty("JButton.buttonType", "roundRect");
        cambiarPerfilBtn.addActionListener(e -> mostrarPantallaInicio());

        headerPanel.add(infoPanel, BorderLayout.WEST);
        headerPanel.add(cambiarPerfilBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel de estadísticas (materias faltantes)
        JPanel statsPanel = crearPanelEstadisticas();

        // Panel principal con tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));

        // Tab 1: Materias Aprobadas
        tabbedPane.addTab("Materias Aprobadas", crearPanelMateriasAprobadas());

        // Tab 2: Materias Disponibles y Actualizar (combinado)
        tabbedPane.addTab("Materias Disponibles / Actualizar", crearPanelMateriasDisponiblesYActualizar());

        // Tab 3: Progreso y proyección de semestres
        tabbedPane.addTab("Progreso", crearPanelProgreso());

        JPanel tabsContainer = new JPanel(new BorderLayout());
        tabsContainer.setOpaque(false);
        tabsContainer.add(tabbedPane, BorderLayout.CENTER);
        tabsContainer.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        // El panel de stats va arriba con su altura natural; el de tabs ocupa todo
        // el resto y es el que scrollea, así una lista larga no lo empuja ni tapa
        // las estadísticas (antes stats y tabs competían por el mismo espacio, y
        // stats perdía cuando la lista de materias crecía).
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.add(statsPanel, BorderLayout.NORTH);
        contenido.add(tabsContainer, BorderLayout.CENTER);
        panel.add(contenido, BorderLayout.CENTER);

        mainFrame.getContentPane().add(panel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private static JPanel crearPanelMateriasAprobadas() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Tema.RADIO_TARJETA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(Tema.SUPERFICIE);
        panel.setColorBorde(Tema.BORDE);

        List<String> materiasAprobadas = materiaService.obtenerMateriasAprobadas(usuarioActual.getUsername());
        Map<String, Materia> todasLasMaterias = materiaService.obtenerTodasLasMaterias();

        JLabel tituloLabel = new JLabel("Materias aprobadas (" + materiasAprobadas.size() + ")");
        tituloLabel.setFont(new Font(Tema.FUENTE, Font.BOLD, 14));
        tituloLabel.setForeground(Tema.TEXTO_MUTED);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 12, 0));

        JPanel listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(Tema.SUPERFICIE);

        if (materiasAprobadas.isEmpty()) {
            JLabel vacioLabel = new JLabel("Todavía no marcaste ninguna materia como aprobada.");
            vacioLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
            vacioLabel.setForeground(Tema.TEXTO_MUTED);
            listaPanel.add(vacioLabel);
        }

        for (String codigo : materiasAprobadas) {
            Materia materia = todasLasMaterias.get(codigo);
            String nombre = materia != null ? materia.getNombre() : codigo;
            String carrera = obtenerCarreraMateria(codigo);
            boolean ambas = "AMBAS".equals(carrera);
            Color colorFondo = ambas ? Tema.TECNICATURA_CLARO : Tema.LICENCIATURA_CLARO;
            Color colorAcento = ambas ? Tema.TECNICATURA : Tema.LICENCIATURA;

            RoundedPanel fila = new RoundedPanel(new BorderLayout(), Tema.RADIO_CHICO);
            fila.setBackground(colorFondo);
            fila.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 6));
            fila.setAlignmentX(Component.LEFT_ALIGNMENT);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

            JLabel nombreLabel = new JLabel(nombre);
            nombreLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 14));
            nombreLabel.setForeground(Tema.TEXTO);

            JLabel carreraLabel = new JLabel(ambas ? "Ambas" : "Licenciatura");
            carreraLabel.setFont(new Font(Tema.FUENTE, Font.BOLD, 11));
            carreraLabel.setForeground(colorAcento);
            carreraLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

            JButton borrarBtn = new JButton("✕");
            borrarBtn.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
            borrarBtn.setForeground(Tema.TEXTO_MUTED);
            borrarBtn.setContentAreaFilled(false);
            borrarBtn.setBorderPainted(false);
            borrarBtn.setFocusPainted(false);
            borrarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            borrarBtn.setToolTipText("Eliminar de materias aprobadas");
            borrarBtn.addActionListener(e -> {
                int confirmacion = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "¿Eliminar \"" + nombre + "\" de tus materias aprobadas?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirmacion == JOptionPane.YES_OPTION) {
                    materiaService.eliminarMateriaAprobada(usuarioActual.getUsername(), codigo);
                    mostrarDashboard(); // Refrescar para actualizar estadísticas
                }
            });

            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            derecha.setOpaque(false);
            derecha.add(carreraLabel);
            derecha.add(borrarBtn);

            fila.add(nombreLabel, BorderLayout.CENTER);
            fila.add(derecha, BorderLayout.EAST);

            listaPanel.add(fila);
            listaPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JScrollPane scrollPane = new JScrollPane(listaPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Tema.SUPERFICIE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        panel.setOpaque(false);

        List<String> materiasAprobadas = materiaService.obtenerMateriasAprobadas(usuarioActual.getUsername());
        Map<String, Materia> todasLasMaterias = materiaService.obtenerTodasLasMaterias();

        // Calcular materias de Tecnicatura aprobadas (solo las que pertenecen a ese plan)
        long materiasTecnicaturaAprobadas = materiasAprobadas.stream()
            .filter(MATERIAS_TECNICATURA::contains)
            .count();
        int faltantesTecnicatura = Math.max(0, TOTAL_MATERIAS_TECNICATURA - (int)materiasTecnicaturaAprobadas);

        // Calcular materias de Licenciatura aprobadas (todas las materias del plan)
        long materiasLicenciaturaAprobadas = materiasAprobadas.stream()
            .filter(codigo -> todasLasMaterias.containsKey(codigo))
            .count();
        int faltantesLicenciatura = Math.max(0, TOTAL_MATERIAS_LICENCIATURA - (int)materiasLicenciaturaAprobadas);

        // Panel Tecnicatura
        RoundedPanel panelTecnicatura = new RoundedPanel(new BorderLayout(), Tema.RADIO_TARJETA);
        panelTecnicatura.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panelTecnicatura.setBackground(Tema.SUPERFICIE);
        panelTecnicatura.setColorBorde(Tema.BORDE);

        JLabel tituloTec = new JLabel("Tecnicatura Universitaria en Informática", SwingConstants.CENTER);
        tituloTec.setFont(new Font(Tema.FUENTE, Font.BOLD, 14));
        tituloTec.setForeground(Tema.TECNICATURA);

        JLabel infoTec = new JLabel("<html><center>Materias aprobadas: " + materiasTecnicaturaAprobadas + "/" + TOTAL_MATERIAS_TECNICATURA +
            "<br>Materias faltantes: <b>" + faltantesTecnicatura + "</b></center></html>", SwingConstants.CENTER);
        infoTec.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        infoTec.setForeground(Tema.TEXTO);
        infoTec.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panelTecnicatura.add(tituloTec, BorderLayout.NORTH);
        panelTecnicatura.add(infoTec, BorderLayout.CENTER);

        // Panel Licenciatura
        RoundedPanel panelLicenciatura = new RoundedPanel(new BorderLayout(), Tema.RADIO_TARJETA);
        panelLicenciatura.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panelLicenciatura.setBackground(Tema.SUPERFICIE);
        panelLicenciatura.setColorBorde(Tema.BORDE);

        JLabel tituloLic = new JLabel("Licenciatura en Sistemas", SwingConstants.CENTER);
        tituloLic.setFont(new Font(Tema.FUENTE, Font.BOLD, 14));
        tituloLic.setForeground(Tema.LICENCIATURA);

        JLabel infoLic = new JLabel("<html><center>Materias aprobadas: " + materiasLicenciaturaAprobadas + "/" + TOTAL_MATERIAS_LICENCIATURA +
            "<br>Materias faltantes: <b>" + faltantesLicenciatura + "</b></center></html>", SwingConstants.CENTER);
        infoLic.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        infoLic.setForeground(Tema.TEXTO);
        infoLic.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panelLicenciatura.add(tituloLic, BorderLayout.NORTH);
        panelLicenciatura.add(infoLic, BorderLayout.CENTER);

        panel.add(panelTecnicatura);
        panel.add(panelLicenciatura);

        return panel;
    }

    private static JPanel crearPanelMateriasDisponiblesYActualizar() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Tema.RADIO_TARJETA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(Tema.SUPERFICIE);
        panel.setColorBorde(Tema.BORDE);

        List<Materia> materiasDisponibles = materiaService.obtenerMateriasDisponibles(usuarioActual);
        // Las compartidas (Ambas) primero, así se ven antes que las exclusivas de Licenciatura.
        materiasDisponibles.sort(Comparator.comparing(
            materia -> "AMBAS".equals(obtenerCarreraMateria(materia.getCodigo())) ? 0 : 1
        ));
        List<String> materiasAprobadas = materiaService.obtenerMateriasAprobadas(usuarioActual.getUsername());

        // Panel superior con información
        JLabel infoLabel = new JLabel("<html>Estas son las materias disponibles para cursar según tus materias aprobadas y correlativas.<br>" +
            "Marca las que has aprobado y haz clic en 'Guardar'.<br>" +
            "<b>Total disponible: " + materiasDisponibles.size() + " materias</b><br>" +
            "<span style='color:#3B6FE0;'>● Azul = Tecnicatura y Licenciatura</span> &nbsp; " +
            "<span style='color:#0E9F5C;'>● Verde = Solo Licenciatura</span></html>");
        infoLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 12));
        infoLabel.setForeground(Tema.TEXTO_MUTED);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        // Panel de checkboxes
        JPanel checkboxesPanel = new JPanel();
        checkboxesPanel.setLayout(new BoxLayout(checkboxesPanel, BoxLayout.Y_AXIS));
        checkboxesPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        checkboxesPanel.setBackground(Tema.SUPERFICIE);

        for (Materia materia : materiasDisponibles) {
            String carrera = obtenerCarreraMateria(materia.getCodigo());
            boolean ambas = "AMBAS".equals(carrera);
            Color colorFondo = ambas ? Tema.TECNICATURA_CLARO : Tema.LICENCIATURA_CLARO;
            Color colorAcento = ambas ? Tema.TECNICATURA : Tema.LICENCIATURA;

            RoundedPanel materiaPanel = new RoundedPanel(new BorderLayout(), Tema.RADIO_CHICO);
            materiaPanel.setBackground(colorFondo);
            materiaPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            materiaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            materiaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

            JCheckBox checkBox = new JCheckBox(materia.getCodigo() + " - " + materia.getNombre());
            checkBox.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
            checkBox.setSelected(materiasAprobadas.contains(materia.getCodigo()));
            checkBox.setBackground(colorFondo);
            checkBox.setForeground(Tema.TEXTO);
            checkBox.setOpaque(false);

            // Etiqueta de carrera
            JLabel carreraLabel = new JLabel(ambas ? "Ambas" : "Licenciatura");
            carreraLabel.setFont(new Font(Tema.FUENTE, Font.BOLD, 11));
            carreraLabel.setForeground(colorAcento);

            materiaPanel.add(checkBox, BorderLayout.CENTER);
            materiaPanel.add(carreraLabel, BorderLayout.EAST);

            checkboxesPanel.add(materiaPanel);
            checkboxesPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JScrollPane scrollPane = new JScrollPane(checkboxesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Tema.SUPERFICIE);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Tema.BORDE),
            "Materias Disponibles para Cursar / Actualizar (Total: " + materiasDisponibles.size() + ")",
            0, 0,
            new Font(Tema.FUENTE, Font.BOLD, 13),
            Tema.TEXTO_MUTED
        ));

        JButton guardarBtn = new JButton("Guardar materias aprobadas");
        guardarBtn.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        guardarBtn.setBackground(Tema.LICENCIATURA);
        guardarBtn.setForeground(Color.WHITE);
        guardarBtn.setMargin(new Insets(10, 22, 10, 22));
        guardarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guardarBtn.setFocusPainted(false);
        guardarBtn.putClientProperty("JButton.buttonType", "roundRect");
        guardarBtn.addActionListener(e -> {
            List<String> materiasSeleccionadas = new java.util.ArrayList<>();
            // Buscar checkboxes dentro de los paneles
            for (Component comp : checkboxesPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel materiaPanel = (JPanel) comp;
                    for (Component subComp : materiaPanel.getComponents()) {
                        if (subComp instanceof JCheckBox) {
                            JCheckBox checkBox = (JCheckBox) subComp;
                            if (checkBox.isSelected()) {
                                String texto = checkBox.getText();
                                String codigo = texto.split(" - ")[0];
                                materiasSeleccionadas.add(codigo);
                            }
                        }
                    }
                }
            }

            materiaService.actualizarMateriasAprobadas(usuarioActual.getUsername(), materiasSeleccionadas);
            JOptionPane.showMessageDialog(mainFrame,
                "Materias actualizadas correctamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            mostrarDashboard(); // Refrescar
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        buttonPanel.add(guardarBtn);

        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel crearPanelProgreso() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Tema.RADIO_TARJETA);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(Tema.SUPERFICIE);
        panel.setColorBorde(Tema.BORDE);

        List<String> materiasAprobadas = materiaService.obtenerMateriasAprobadas(usuarioActual.getUsername());
        Map<String, Materia> todasLasMaterias = materiaService.obtenerTodasLasMaterias();

        long tecAprobadas = materiasAprobadas.stream().filter(MATERIAS_TECNICATURA::contains).count();
        int porcentajeTec = (int) Math.round(100.0 * tecAprobadas / TOTAL_MATERIAS_TECNICATURA);

        long licAprobadas = materiasAprobadas.stream().filter(todasLasMaterias::containsKey).count();
        int porcentajeLic = (int) Math.round(100.0 * licAprobadas / TOTAL_MATERIAS_LICENCIATURA);

        JPanel graficos = new JPanel(new FlowLayout(FlowLayout.CENTER, 48, 0));
        graficos.setOpaque(false);
        graficos.add(new DonutChart(porcentajeTec, Tema.TECNICATURA, "Tecnicatura"));
        graficos.add(new DonutChart(porcentajeLic, Tema.LICENCIATURA, "Licenciatura"));

        JLabel caminoTitulo = new JLabel("Camino mínimo para recibirte");
        caminoTitulo.setFont(new Font(Tema.FUENTE, Font.BOLD, 14));
        caminoTitulo.setForeground(Tema.TEXTO_MUTED);

        JLabel caminoSub = new JLabel(
            "<html>Estimación respetando correlativas: en cada semestre se priorizan las materias<br>"
            + "que más habilitan el resto del plan. Es una referencia, no una garantía.</html>");
        caminoSub.setFont(new Font(Tema.FUENTE, Font.PLAIN, 12));
        caminoSub.setForeground(Tema.TEXTO_MUTED);

        JPanel caminoHeader = new JPanel(new BorderLayout(0, 4));
        caminoHeader.setOpaque(false);
        caminoHeader.add(caminoTitulo, BorderLayout.NORTH);
        caminoHeader.add(caminoSub, BorderLayout.CENTER);
        caminoHeader.setBorder(BorderFactory.createEmptyBorder(24, 0, 12, 0));

        JPanel tablaPanel = new JPanel();
        tablaPanel.setLayout(new BoxLayout(tablaPanel, BoxLayout.Y_AXIS));
        tablaPanel.setOpaque(false);

        tablaPanel.add(crearFilaCamino("Ritmo", "Tecnicatura", "Licenciatura", true));
        tablaPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        for (int ritmo = 1; ritmo <= 4; ritmo++) {
            int semestresTec = materiaService.estimarSemestresRestantes(MATERIAS_TECNICATURA, materiasAprobadas, ritmo);
            int semestresLic = materiaService.estimarSemestresRestantes(todasLasMaterias.keySet(), materiasAprobadas, ritmo);
            String etiquetaRitmo = ritmo + (ritmo == 1 ? " materia / semestre" : " materias / semestre");
            tablaPanel.add(crearFilaCamino(etiquetaRitmo, formatearSemestres(semestresTec), formatearSemestres(semestresLic), false));
            tablaPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        // Detalle: qué materias tocarían en cada semestre, para una carrera y ritmo elegidos.
        JLabel detalleTitulo = new JLabel("Ver el camino paso a paso");
        detalleTitulo.setFont(new Font(Tema.FUENTE, Font.BOLD, 14));
        detalleTitulo.setForeground(Tema.TEXTO_MUTED);
        detalleTitulo.setBorder(BorderFactory.createEmptyBorder(24, 0, 10, 0));

        JComboBox<String> carreraCombo = new JComboBox<>(new String[]{"Tecnicatura", "Licenciatura"});
        carreraCombo.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));

        JComboBox<Integer> ritmoCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        ritmoCombo.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        ritmoCombo.setSelectedItem(2);

        JPanel selectoresPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectoresPanel.setOpaque(false);
        JLabel etiquetaCarrera = new JLabel("Carrera:");
        etiquetaCarrera.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        etiquetaCarrera.setForeground(Tema.TEXTO_MUTED);
        JLabel etiquetaRitmoCombo = new JLabel("Ritmo:");
        etiquetaRitmoCombo.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        etiquetaRitmoCombo.setForeground(Tema.TEXTO_MUTED);
        etiquetaRitmoCombo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        selectoresPanel.add(etiquetaCarrera);
        selectoresPanel.add(carreraCombo);
        selectoresPanel.add(etiquetaRitmoCombo);
        selectoresPanel.add(ritmoCombo);

        JPanel detalleContainer = new JPanel();
        detalleContainer.setLayout(new BoxLayout(detalleContainer, BoxLayout.Y_AXIS));
        detalleContainer.setOpaque(false);
        detalleContainer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        Runnable actualizarDetalle = () -> {
            detalleContainer.removeAll();
            boolean esTecnicatura = "Tecnicatura".equals(carreraCombo.getSelectedItem());
            int ritmoElegido = (Integer) ritmoCombo.getSelectedItem();
            Collection<String> codigosPlan = esTecnicatura ? MATERIAS_TECNICATURA : todasLasMaterias.keySet();
            Color colorCarrera = esTecnicatura ? Tema.TECNICATURA : Tema.LICENCIATURA;
            Color colorCarreraClaro = esTecnicatura ? Tema.TECNICATURA_CLARO : Tema.LICENCIATURA_CLARO;

            List<List<Materia>> camino = materiaService.planificarCamino(codigosPlan, materiasAprobadas, ritmoElegido);

            if (camino.isEmpty()) {
                JLabel completoLabel = new JLabel("Ya no te queda ninguna materia pendiente en esta carrera.");
                completoLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
                completoLabel.setForeground(Tema.TEXTO_MUTED);
                detalleContainer.add(completoLabel);
            }
            for (int i = 0; i < camino.size(); i++) {
                String nombres = camino.get(i).stream()
                    .map(Materia::getNombre)
                    .collect(java.util.stream.Collectors.joining(", "));

                RoundedPanel filaSemestre = new RoundedPanel(new BorderLayout(12, 0), Tema.RADIO_CHICO);
                filaSemestre.setBackground(colorCarreraClaro);
                filaSemestre.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
                filaSemestre.setAlignmentX(Component.LEFT_ALIGNMENT);
                filaSemestre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JLabel semestreLabel = new JLabel("Semestre " + (i + 1));
                semestreLabel.setFont(new Font(Tema.FUENTE, Font.BOLD, 13));
                semestreLabel.setForeground(colorCarrera);
                semestreLabel.setPreferredSize(new Dimension(110, semestreLabel.getPreferredSize().height));

                JLabel nombresLabel = new JLabel(nombres);
                nombresLabel.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
                nombresLabel.setForeground(Tema.TEXTO);

                filaSemestre.add(semestreLabel, BorderLayout.WEST);
                filaSemestre.add(nombresLabel, BorderLayout.CENTER);

                detalleContainer.add(filaSemestre);
                detalleContainer.add(Box.createRigidArea(new Dimension(0, 8)));
            }

            detalleContainer.revalidate();
            detalleContainer.repaint();
        };

        carreraCombo.addActionListener(e -> actualizarDetalle.run());
        ritmoCombo.addActionListener(e -> actualizarDetalle.run());
        actualizarDetalle.run();

        JPanel detalleSeccion = new JPanel();
        detalleSeccion.setLayout(new BoxLayout(detalleSeccion, BoxLayout.Y_AXIS));
        detalleSeccion.setOpaque(false);
        detalleSeccion.add(detalleTitulo);
        detalleSeccion.add(selectoresPanel);
        detalleSeccion.add(detalleContainer);

        JPanel contenidoVertical = new JPanel();
        contenidoVertical.setLayout(new BoxLayout(contenidoVertical, BoxLayout.Y_AXIS));
        contenidoVertical.setOpaque(false);
        contenidoVertical.add(tablaPanel);
        contenidoVertical.add(detalleSeccion);

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.setOpaque(false);
        abajo.add(caminoHeader, BorderLayout.NORTH);
        abajo.add(contenidoVertical, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(abajo);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Tema.SUPERFICIE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(graficos, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static String formatearSemestres(int semestres) {
        if (semestres == 0) {
            return "¡Completo!";
        }
        double anios = semestres / 2.0;
        String textoAnios = anios == Math.floor(anios)
            ? (int) anios + (anios == 1 ? " año" : " años")
            : String.format(java.util.Locale.forLanguageTag("es"), "%.1f años", anios);
        return semestres + (semestres == 1 ? " semestre" : " semestres") + " (~" + textoAnios + ")";
    }

    private static JPanel crearFilaCamino(String colRitmo, String colTec, String colLic, boolean esEncabezado) {
        JPanel fila = new JPanel(new GridLayout(1, 3, 16, 0));
        fila.setOpaque(false);
        fila.add(crearCeldaCamino(colRitmo, esEncabezado, Tema.TEXTO));
        fila.add(crearCeldaCamino(colTec, esEncabezado, Tema.TECNICATURA));
        fila.add(crearCeldaCamino(colLic, esEncabezado, Tema.LICENCIATURA));
        return fila;
    }

    private static JLabel crearCeldaCamino(String texto, boolean esEncabezado, Color colorTexto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font(Tema.FUENTE, esEncabezado ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(esEncabezado ? Tema.TEXTO_MUTED : colorTexto);
        return label;
    }
}

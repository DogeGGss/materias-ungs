package com.universidad.demo;

import com.formdev.flatlaf.FlatLightLaf;
import com.universidad.demo.models.Materia;
import com.universidad.demo.models.Usuario;
import com.universidad.demo.services.MateriaService;
import com.universidad.demo.ui.Avatares;
import com.universidad.demo.ui.RoundedPanel;
import com.universidad.demo.ui.Tema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
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
        panel.add(statsPanel, BorderLayout.CENTER);

        // Panel principal con tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));

        // Tab 1: Materias Aprobadas
        tabbedPane.addTab("Materias Aprobadas", crearPanelMateriasAprobadas());

        // Tab 2: Materias Disponibles y Actualizar (combinado)
        tabbedPane.addTab("Materias Disponibles / Actualizar", crearPanelMateriasDisponiblesYActualizar());

        JPanel tabsContainer = new JPanel(new BorderLayout());
        tabsContainer.setOpaque(false);
        tabsContainer.add(tabbedPane, BorderLayout.CENTER);
        tabsContainer.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        panel.add(tabsContainer, BorderLayout.SOUTH);

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

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String codigo : materiasAprobadas) {
            Materia materia = todasLasMaterias.get(codigo);
            if (materia != null) {
                listModel.addElement(codigo + " - " + materia.getNombre());
            } else {
                listModel.addElement(codigo); // Si no se encuentra, mostrar solo el código
            }
        }

        JList<String> lista = new JList<>(listModel);
        lista.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permite selección múltiple con Shift+Click
        lista.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        lista.setBackground(Tema.SUPERFICIE);
        // Configurar colores de selección
        lista.setSelectionBackground(Tema.TECNICATURA);
        lista.setSelectionForeground(Color.WHITE);
        lista.setFixedCellHeight(28);
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Tema.BORDE),
            "Lista de Materias Aprobadas (Total: " + materiasAprobadas.size() + ")",
            0, 0,
            new Font(Tema.FUENTE, Font.BOLD, 13),
            Tema.TEXTO_MUTED
        ));

        JButton eliminarBtn = new JButton("Eliminar materias seleccionadas");
        eliminarBtn.setFont(new Font(Tema.FUENTE, Font.PLAIN, 13));
        eliminarBtn.setBackground(Tema.PELIGRO);
        eliminarBtn.setForeground(Color.WHITE);
        eliminarBtn.setMargin(new Insets(10, 22, 10, 22));
        eliminarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eliminarBtn.setFocusPainted(false);
        eliminarBtn.putClientProperty("JButton.buttonType", "roundRect");
        eliminarBtn.addActionListener(e -> {
            List<String> seleccionadas = lista.getSelectedValuesList();
            if (seleccionadas != null && !seleccionadas.isEmpty()) {
                // Construir mensaje de confirmación
                String mensaje;
                if (seleccionadas.size() == 1) {
                    String codigo = seleccionadas.get(0).split(" - ")[0];
                    Materia materia = todasLasMaterias.get(codigo);
                    String nombreMateria = materia != null ? materia.getNombre() : codigo;
                    mensaje = "¿Estás seguro de eliminar la materia: " + nombreMateria + "?";
                } else {
                    mensaje = "¿Estás seguro de eliminar las " + seleccionadas.size() + " materias seleccionadas?";
                }

                int confirmacion = JOptionPane.showConfirmDialog(
                    mainFrame,
                    mensaje,
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirmacion == JOptionPane.YES_OPTION) {
                    // Eliminar todas las materias seleccionadas
                    for (String seleccionada : seleccionadas) {
                        String codigo = seleccionada.split(" - ")[0];
                        materiaService.eliminarMateriaAprobada(usuarioActual.getUsername(), codigo);
                        listModel.removeElement(seleccionada);
                    }

                    String mensajeExito = seleccionadas.size() == 1
                        ? "Materia eliminada correctamente"
                        : seleccionadas.size() + " materias eliminadas correctamente";

                    JOptionPane.showMessageDialog(mainFrame,
                        mensajeExito,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    mostrarDashboard(); // Refrescar para actualizar estadísticas
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                    "Por favor selecciona al menos una materia\n(Usa Shift+Click para seleccionar múltiples)",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        buttonPanel.add(eliminarBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

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
}

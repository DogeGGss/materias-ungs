package com.universidad.demo;

import com.universidad.demo.models.Materia;
import com.universidad.demo.models.Usuario;
import com.universidad.demo.services.MateriaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
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
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 250));
        
        // Título
        JLabel titulo = new JLabel("Selecciona un perfil para comenzar", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Panel de usuarios
        JPanel usuariosPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        usuariosPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        usuariosPanel.setBackground(new Color(245, 245, 250));
        
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
        boton.setPreferredSize(new Dimension(250, 320));
        boton.setBackground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Cargar imagen del usuario
        ImageIcon imagenIcon = null;
        try {
            // Las imágenes están en src/main/resources/static/img/
            // En el classpath están disponibles como /static/img/archivo.jpg
            String rutaImagen = usuario.getAvatarUrl();
            // Normalizar la ruta: /img/admin.jpg -> /static/img/admin.jpg
            if (rutaImagen.startsWith("/img/")) {
                rutaImagen = "/static" + rutaImagen;
            } else if (!rutaImagen.startsWith("/static/")) {
                rutaImagen = "/static" + (rutaImagen.startsWith("/") ? rutaImagen : "/" + rutaImagen);
            }
            
            InputStream imgStream = DemoApplication.class.getResourceAsStream(rutaImagen);
            if (imgStream != null) {
                BufferedImage img = ImageIO.read(imgStream);
                if (img != null) {
                    // Redimensionar imagen a 130x130 manteniendo proporción
                    Image imgEscalada = img.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                    imagenIcon = new ImageIcon(imgEscalada);
                }
                imgStream.close();
            } else {
                System.out.println("No se encontró la imagen en: " + rutaImagen);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + usuario.getAvatarUrl() + " - " + e.getMessage());
            e.printStackTrace();
        }
        
        // Panel para la imagen
        JLabel imagenLabel = new JLabel();
        if (imagenIcon != null) {
            imagenLabel.setIcon(imagenIcon);
        } else {
            // Si no hay imagen, mostrar un icono por defecto
            imagenLabel.setText("👤");
            imagenLabel.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        }
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Nombre
        JLabel nombreLabel = new JLabel(usuario.getNombreCompleto(), SwingConstants.CENTER);
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        // Rol
        JLabel rolLabel = new JLabel(usuario.getRol(), SwingConstants.CENTER);
        rolLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        rolLabel.setForeground(new Color(100, 100, 100));
        rolLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(nombreLabel, BorderLayout.CENTER);
        infoPanel.add(rolLabel, BorderLayout.SOUTH);
        
        JPanel contenidoPanel = new JPanel(new BorderLayout());
        contenidoPanel.setOpaque(false);
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
                boton.setBackground(new Color(240, 240, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(Color.WHITE);
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
        panel.setBackground(new Color(245, 245, 250));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20),
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(50, 100, 150))
        ));
        
        // Cargar imagen del usuario para el dashboard
        ImageIcon imagenIcon = null;
        try {
            // Las imágenes están en src/main/resources/static/img/
            // En el classpath están disponibles como /static/img/archivo.jpg
            String rutaImagen = usuarioActual.getAvatarUrl();
            // Normalizar la ruta: /img/admin.jpg -> /static/img/admin.jpg
            if (rutaImagen.startsWith("/img/")) {
                rutaImagen = "/static" + rutaImagen;
            } else if (!rutaImagen.startsWith("/static/")) {
                rutaImagen = "/static" + (rutaImagen.startsWith("/") ? rutaImagen : "/" + rutaImagen);
            }
            
            InputStream imgStream = DemoApplication.class.getResourceAsStream(rutaImagen);
            if (imgStream != null) {
                BufferedImage img = ImageIO.read(imgStream);
                if (img != null) {
                    // Redimensionar imagen a 100x100 para el header
                    Image imgEscalada = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imagenIcon = new ImageIcon(imgEscalada);
                }
                imgStream.close();
            } else {
                System.out.println("No se encontró la imagen en: " + rutaImagen);
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + usuarioActual.getAvatarUrl() + " - " + e.getMessage());
            e.printStackTrace();
        }
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        infoPanel.setOpaque(false);
        
        // Imagen del usuario en el header
        JLabel imagenHeaderLabel = new JLabel();
        if (imagenIcon != null) {
            imagenHeaderLabel.setIcon(imagenIcon);
        } else {
            imagenHeaderLabel.setText("👤");
            imagenHeaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        }
        imagenHeaderLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JPanel textoPanel = new JPanel(new BorderLayout());
        textoPanel.setOpaque(false);
        JLabel nombreLabel = new JLabel(usuarioActual.getNombreCompleto());
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nombreLabel.setForeground(Color.WHITE);
        JLabel rolLabel = new JLabel(usuarioActual.getRol());
        rolLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        rolLabel.setForeground(new Color(220, 220, 220));
        textoPanel.add(nombreLabel, BorderLayout.NORTH);
        textoPanel.add(rolLabel, BorderLayout.SOUTH);
        
        infoPanel.add(imagenHeaderLabel);
        infoPanel.add(textoPanel);
        
        JButton cambiarPerfilBtn = new JButton("Cambiar Perfil");
        cambiarPerfilBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cambiarPerfilBtn.setBackground(new Color(255, 255, 255));
        cambiarPerfilBtn.setForeground(new Color(70, 130, 180));
        cambiarPerfilBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cambiarPerfilBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cambiarPerfilBtn.addActionListener(e -> mostrarPantallaInicio());
        
        headerPanel.add(infoPanel, BorderLayout.WEST);
        headerPanel.add(cambiarPerfilBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel de estadísticas (materias faltantes)
        JPanel statsPanel = crearPanelEstadisticas();
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Panel principal con tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Tab 1: Materias Aprobadas
        tabbedPane.addTab("Materias Aprobadas", crearPanelMateriasAprobadas());
        
        // Tab 2: Materias Disponibles y Actualizar (combinado)
        tabbedPane.addTab("Materias Disponibles / Actualizar", crearPanelMateriasDisponiblesYActualizar());
        
        JPanel tabsContainer = new JPanel(new BorderLayout());
        tabsContainer.add(tabbedPane, BorderLayout.CENTER);
        tabsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(tabsContainer, BorderLayout.SOUTH);
        
        mainFrame.getContentPane().add(panel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    private static JPanel crearPanelMateriasAprobadas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);
        
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
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permite selección múltiple con Shift+Click
        lista.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        // Configurar colores de selección
        lista.setSelectionBackground(new Color(70, 130, 180)); // Azul para selección
        lista.setSelectionForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Lista de Materias Aprobadas (Total: " + materiasAprobadas.size() + ")",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 13)
        ));
        
        JButton eliminarBtn = new JButton("Eliminar Materias Seleccionadas");
        eliminarBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eliminarBtn.setBackground(new Color(220, 53, 69));
        eliminarBtn.setForeground(Color.WHITE);
        eliminarBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        eliminarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(eliminarBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private static JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 250));
        
        List<String> materiasAprobadas = materiaService.obtenerMateriasAprobadas(usuarioActual.getUsername());
        Map<String, Materia> todasLasMaterias = materiaService.obtenerTodasLasMaterias();
        
        // Calcular materias de Tecnicatura aprobadas
        long materiasTecnicaturaAprobadas = materiasAprobadas.stream()
            .filter(codigo -> todasLasMaterias.containsKey(codigo))
            .count();
        int faltantesTecnicatura = Math.max(0, TOTAL_MATERIAS_TECNICATURA - (int)materiasTecnicaturaAprobadas);
        
        // Calcular materias de Licenciatura aprobadas (todas las materias del plan)
        long materiasLicenciaturaAprobadas = materiasAprobadas.stream()
            .filter(codigo -> todasLasMaterias.containsKey(codigo))
            .count();
        int faltantesLicenciatura = Math.max(0, TOTAL_MATERIAS_LICENCIATURA - (int)materiasLicenciaturaAprobadas);
        
        // Panel Tecnicatura
        JPanel panelTecnicatura = new JPanel(new BorderLayout());
        panelTecnicatura.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelTecnicatura.setBackground(Color.WHITE);
        
        JLabel tituloTec = new JLabel("Tecnicatura Universitaria en Informática", SwingConstants.CENTER);
        tituloTec.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloTec.setForeground(new Color(70, 130, 180));
        
        JLabel infoTec = new JLabel("<html><center>Materias aprobadas: " + materiasTecnicaturaAprobadas + "/" + TOTAL_MATERIAS_TECNICATURA + 
            "<br>Materias faltantes: <b>" + faltantesTecnicatura + "</b></center></html>", SwingConstants.CENTER);
        infoTec.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoTec.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        panelTecnicatura.add(tituloTec, BorderLayout.NORTH);
        panelTecnicatura.add(infoTec, BorderLayout.CENTER);
        
        // Panel Licenciatura
        JPanel panelLicenciatura = new JPanel(new BorderLayout());
        panelLicenciatura.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 167, 69), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelLicenciatura.setBackground(Color.WHITE);
        
        JLabel tituloLic = new JLabel("Licenciatura en Sistemas", SwingConstants.CENTER);
        tituloLic.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLic.setForeground(new Color(40, 167, 69));
        
        JLabel infoLic = new JLabel("<html><center>Materias aprobadas: " + materiasLicenciaturaAprobadas + "/" + TOTAL_MATERIAS_LICENCIATURA + 
            "<br>Materias faltantes: <b>" + faltantesLicenciatura + "</b></center></html>", SwingConstants.CENTER);
        infoLic.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLic.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        panelLicenciatura.add(tituloLic, BorderLayout.NORTH);
        panelLicenciatura.add(infoLic, BorderLayout.CENTER);
        
        panel.add(panelTecnicatura);
        panel.add(panelLicenciatura);
        
        return panel;
    }
    
    private static JPanel crearPanelMateriasDisponiblesYActualizar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);
        
        List<Materia> materiasDisponibles = materiaService.obtenerMateriasDisponibles(usuarioActual);
        List<String> materiasAprobadas = materiaService.obtenerMateriasAprobadas(usuarioActual.getUsername());
        
        // Panel superior con información
        JLabel infoLabel = new JLabel("<html>Estas son las materias disponibles para cursar según tus materias aprobadas y correlativas.<br>" +
            "Marca las que has aprobado y haz clic en 'Guardar'.<br>" +
            "<b>Total disponible: " + materiasDisponibles.size() + " materias</b><br>" +
            "<span style='color:#4682B4;'>🔵 Azul = Tecnicatura y Licenciatura</span> | " +
            "<span style='color:#28A745;'>🟢 Verde = Solo Licenciatura</span></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Panel de checkboxes
        JPanel checkboxesPanel = new JPanel();
        checkboxesPanel.setLayout(new BoxLayout(checkboxesPanel, BoxLayout.Y_AXIS));
        checkboxesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        checkboxesPanel.setBackground(Color.WHITE);
        
        for (Materia materia : materiasDisponibles) {
            String carrera = obtenerCarreraMateria(materia.getCodigo());
            Color colorFondo;
            Color colorTexto = Color.BLACK;
            
            if ("AMBAS".equals(carrera)) {
                colorFondo = new Color(230, 240, 255); // Azul claro para ambas
            } else {
                colorFondo = new Color(230, 255, 240); // Verde claro para solo Licenciatura
            }
            
            JPanel materiaPanel = new JPanel(new BorderLayout());
            materiaPanel.setBackground(colorFondo);
            materiaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            JCheckBox checkBox = new JCheckBox(materia.getCodigo() + " - " + materia.getNombre());
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            checkBox.setSelected(materiasAprobadas.contains(materia.getCodigo()));
            checkBox.setBackground(colorFondo);
            checkBox.setForeground(colorTexto);
            checkBox.setOpaque(false);
            
            // Etiqueta de carrera
            JLabel carreraLabel = new JLabel(carrera.equals("AMBAS") ? "🔵 Ambas" : "🟢 Licenciatura");
            carreraLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            carreraLabel.setForeground(carrera.equals("AMBAS") ? new Color(70, 130, 180) : new Color(40, 167, 69));
            
            materiaPanel.add(checkBox, BorderLayout.CENTER);
            materiaPanel.add(carreraLabel, BorderLayout.EAST);
            
            checkboxesPanel.add(materiaPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(checkboxesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Materias Disponibles para Cursar / Actualizar (Total: " + materiasDisponibles.size() + ")",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 13)
        ));
        
        JButton guardarBtn = new JButton("Guardar Materias Aprobadas");
        guardarBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        guardarBtn.setBackground(new Color(40, 167, 69));
        guardarBtn.setForeground(Color.WHITE);
        guardarBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        guardarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(guardarBtn);
        
        panel.add(infoLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}
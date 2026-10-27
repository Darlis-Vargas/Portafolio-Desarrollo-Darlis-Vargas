package com.taller.app.ui;

import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;

import com.taller.app.servicio.AutenticacionServicio;
import com.taller.app.servicio.ReportesServicio;
import com.taller.app.servicio.Sesion;
import com.taller.app.modelo.Usuario;

public class MenuPrincipalFrame {

    private JFrame frame;
    private JPanel panelContenido;
    private CardLayout cardLayout;

    // Los guardamos como atributos porque aplicarPermisos() necesita
    // ocultarlos/mostrarlos después de que se crean.
    private JMenuItem itemUsuarios;
    private JMenuItem itemInventario;
    private JMenuItem itemVentas;
    private JMenuItem itemTaller;
    private JMenuItem itemReportes;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MenuPrincipalFrame window = new MenuPrincipalFrame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MenuPrincipalFrame() {
        initialize();
        aplicarPermisos();
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Taller - Menú Principal");
        frame.getContentPane().setLayout(new BorderLayout());

        // --- Barra de menú superior ---
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu menuModulos = new JMenu("Módulos");
        menuBar.add(menuModulos);

        itemUsuarios = new JMenuItem("Usuarios");
        menuModulos.add(itemUsuarios);
        itemInventario = new JMenuItem("Inventario");
        menuModulos.add(itemInventario);
        itemVentas = new JMenuItem("Ventas");
        menuModulos.add(itemVentas);
        itemTaller = new JMenuItem("Taller");
        menuModulos.add(itemTaller);
        itemReportes = new JMenuItem("Reportes");
        menuModulos.add(itemReportes);

        JMenu menuSesion = new JMenu("Sesión");
        menuBar.add(menuSesion);

        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        itemCerrarSesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sesion.obtenerInstancia().cerrarSesion();
                frame.dispose();
                new LoginFrame().setVisible(true);
            }
        });
        menuSesion.add(itemCerrarSesion);

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuSesion.add(itemSalir);

        // --- Encabezado con el usuario logueado ---
        Usuario activo = Sesion.obtenerInstancia().getUsuarioActivo();
        String textoBienvenida = activo != null
                ? "Bienvenido, " + activo.getNombre() + " (" + activo.getRol() + ")"
                : "Bienvenido";

        JLabel labelBienvenida = new JLabel(textoBienvenida, SwingConstants.LEFT);
        labelBienvenida.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelBienvenida.setOpaque(true);
        labelBienvenida.setBackground(new Color(31, 56, 100));
        labelBienvenida.setForeground(Color.WHITE);
        labelBienvenida.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        frame.getContentPane().add(labelBienvenida, BorderLayout.NORTH);

        // --- Área de contenido central (cambia según el módulo elegido) ---
        cardLayout = new CardLayout();
        panelContenido = new JPanel();
        panelContenido.setLayout(cardLayout);
        frame.getContentPane().add(panelContenido, BorderLayout.CENTER);

        panelContenido.add(new PantallaBienvenida(activo), "Bienvenida");
        panelContenido.add(new UsuariosPanel(new AutenticacionServicio()), "Usuarios");
        panelContenido.add(new InventarioPanel(), "Inventario");
        panelContenido.add(new VentasPanel(), "Ventas");
        panelContenido.add(new TallerPanel(activo), "Taller");
        panelContenido.add(new ReportesPanel(new ReportesServicio(), activo.getRol()), "Reportes");

        // --- Conectar cada opción del menú con el panel que debe mostrar ---
        itemUsuarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenido, "Usuarios");
            }
        });
        itemInventario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenido, "Inventario");
            }
        });
        itemVentas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenido, "Ventas");
            }
        });
        itemTaller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenido, "Taller");
            }
        });
        itemReportes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenido, "Reportes");
            }
        });
    }

    // Método de apoyo para no repetir estas 3 líneas 5 veces.
    private void agregarPanel(JPanel contenedor, String nombre, String textoPlaceholder) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(textoPlaceholder));
        contenedor.add(panel, nombre);
    }

    // Oculta del menú las opciones que el rol logueado no tiene permitidas
    // (no solo las deshabilita — no deben ni aparecer).
    private void aplicarPermisos() {
        itemUsuarios.setVisible(Sesion.obtenerInstancia().tienePermiso("GESTION_USUARIOS"));
        itemInventario.setVisible(Sesion.obtenerInstancia().tienePermiso("VER_INVENTARIO"));
        itemVentas.setVisible(Sesion.obtenerInstancia().tienePermiso("REGISTRAR_VENTAS"));
        itemTaller.setVisible(Sesion.obtenerInstancia().tienePermiso("ACTUALIZAR_ORDEN"));
        itemReportes.setVisible(Sesion.obtenerInstancia().tienePermiso("VER_REPORTES"));
        mostrarPrimerModuloPermitido();
    }

    
    private void mostrarPrimerModuloPermitido() {
        // Todos los roles ven primero la bienvenida, sin importar que
        // modulos tengan permitidos; cambian de pantalla al elegir del menu.
        cardLayout.show(panelContenido, "Bienvenida");
    }
    
    
}
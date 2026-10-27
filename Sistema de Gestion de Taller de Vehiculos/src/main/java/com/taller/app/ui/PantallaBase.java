package com.taller.app.ui;

import javax.swing.*;
import java.awt.*;


// Clase abstracta base para las pantallas principales del sistema.
// Define una estructura comun: un encabezado con titulo, y un area de
// contenido especifico que cada pantalla hija debe construir a su manera.
public abstract class PantallaBase extends JPanel {

    private final String titulo;

    public PantallaBase(String titulo) {
        this.titulo = titulo;
        setLayout(new BorderLayout());
        if (titulo != null && !titulo.isEmpty()) {
            add(crearEncabezado(), BorderLayout.NORTH);
        }
        add(construirContenido(), BorderLayout.CENTER);
    }

    // Cada pantalla hija decide que va en el centro (formularios, tablas, etc.)
    protected abstract JComponent construirContenido();

    private JComponent crearEncabezado() {
        JLabel lbl = new JLabel(titulo, SwingConstants.LEFT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(31, 56, 100));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        return lbl;
    }
}
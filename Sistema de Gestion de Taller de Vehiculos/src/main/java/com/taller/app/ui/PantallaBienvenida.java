package com.taller.app.ui;

import com.taller.app.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

// Pantalla que ven TODOS los roles al iniciar sesion, antes de elegir un
// modulo. Solo implementa construirContenido(); el encabezado ya lo pone
// PantallaBase.
public class PantallaBienvenida extends PantallaBase {

    private final Usuario usuarioActivo;

    public PantallaBienvenida(Usuario usuarioActivo) {
        super("");
        this.usuarioActivo = usuarioActivo;
    }

    @Override
    protected JComponent construirContenido() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(214, 227, 245));
        return panel;
    }
}
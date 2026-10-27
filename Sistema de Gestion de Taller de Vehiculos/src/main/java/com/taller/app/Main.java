package com.taller.app;

import javax.swing.SwingUtilities;
import com.taller.app.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
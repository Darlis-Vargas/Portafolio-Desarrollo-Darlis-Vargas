package com.taller.app.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.taller.app.servicio.AutenticacionServicio;

public class LoginFrame extends JFrame {

    // Identificador que pide Java para clases que "podrían" guardarse como
    // bytes (serializarse). No lo usamos en este proyecto, pero Eclipse lo
    // pide para no dar advertencia; lo dejamos en 1 y ya.
    private static final long serialVersionUID = 1L;

    // Campos de texto: hay que guardarlos como atributos de la clase
    // (no como variables locales) porque intentarLogin() los necesita leer
    // después de que el usuario haga clic en el botón.
    private JTextField campoUsuario;
    private JPasswordField campoPassword;

    public LoginFrame() {
        // Configuración básica de la ventana: título, tamaño, qué hacer al
        // cerrarla, y que aparezca centrada en la pantalla.
        setTitle("Taller - Iniciar sesión");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // GridLayout(3, 2, ...) = una cuadrícula de 3 filas x 2 columnas,
        // con 5px de espacio entre celdas. Cada par de add() ocupa una fila:
        // una etiqueta a la izquierda, el campo a la derecha.
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        // Márgenes internos (arriba, izquierda, abajo, derecha) para que los
        // campos no queden pegados al borde de la ventana.
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Fila 1: etiqueta "Usuario" + campo de texto normal.
        panel.add(new JLabel("Usuario:"));
        campoUsuario = new JTextField();
        panel.add(campoUsuario);

        // Fila 2: etiqueta "Contraseña" + campo especial que oculta lo que
        // se escribe (JPasswordField, en vez de JTextField).
        panel.add(new JLabel("Contraseña:"));
        campoPassword = new JPasswordField();
        panel.add(campoPassword);

        // Fila 3: el botón. Cuando lo presionan, se ejecuta intentarLogin().
        // Se usa una clase anónima (en vez de una lambda) porque así queda
        // compatible con la vista visual de WindowBuilder si algún día la usas.
        JButton botonIngresar = new JButton("Ingresar");
        botonIngresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                intentarLogin();
            }
        });
        panel.add(new JLabel()); // celda vacía, solo para que el botón quede
                                  // alineado con la columna derecha
        panel.add(botonIngresar);

        // El panel con los 3 filas se agrega a la ventana.
        add(panel);
    }

    // Se ejecuta cada vez que el usuario da clic en "Ingresar".
    private void intentarLogin() {
        // Lee lo que el usuario escribió en los dos campos.
        String usuario = campoUsuario.getText();
        String password = new String(campoPassword.getPassword());

        // Le pasa la validación a la capa de servicio — LoginFrame no sabe
        // ni le importa cómo se valida, solo pregunta "¿es válido o no?".
        AutenticacionServicio authServicio = new AutenticacionServicio();
        boolean exito = authServicio.autenticar(usuario, password);

        if (exito) {
            // Login correcto: cierra esta ventana (dispose) y abre el menú
            // principal en su lugar. Sesion ya guardó internamente quién
            // inició sesión (eso lo hizo autenticar() por dentro).
            dispose();
            new MenuPrincipalFrame().mostrar();
        } else {
            // Login incorrecto: solo avisa con un mensaje de error, la
            // ventana de login se queda abierta para reintentar.
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
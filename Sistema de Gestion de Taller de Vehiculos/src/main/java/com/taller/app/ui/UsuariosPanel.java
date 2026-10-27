package com.taller.app.ui;
 
import com.taller.app.modelo.Rol;
import com.taller.app.modelo.Usuario;
import com.taller.app.servicio.AutenticacionServicio;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
 
/**
 * UsuariosPanel
 * Pantalla de gestion de usuarios: crear, editar y activar/desactivar.
 * Paquete: com.taller.app.ui
 *
 * Solo la ve el rol Administrador (eso lo controla MenuPrincipalFrame,
 * ocultando el item de menu, no este panel).
 *
 * Recibe el AutenticacionServicio ya creado por quien lo instancia
 * (a diferencia de otros paneles, que crean su propio servicio adentro).
 */
public class UsuariosPanel extends JPanel {
 
    private final AutenticacionServicio autenticacionServicio;
 
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
 
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<Rol> comboRol;
    private JCheckBox chkActivo;
 
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnCambiarEstado;
 
    // Si es null, el formulario esta en modo "crear nuevo". Si tiene un
    // valor, esta editando ese usuario (se llena al seleccionar una fila).
    private Integer usuarioSeleccionadoId = null;
 
    public UsuariosPanel(AutenticacionServicio autenticacionServicio) {
        this.autenticacionServicio = autenticacionServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        initComponents();
        cargarUsuarios();
    }
 
    private void initComponents() {
        // Formulario de datos
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Gestión de Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
 
        // Fila 0: nombre completo + usuario (username)
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        panelFormulario.add(txtNombre, gbc);
 
        gbc.gridx = 2;
        panelFormulario.add(new JLabel("Usuario (Username):"), gbc);
        gbc.gridx = 3;
        txtUsuario = new JTextField(15);
        panelFormulario.add(txtUsuario, gbc);
 
        // Fila 1: contraseña + rol
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        panelFormulario.add(txtPassword, gbc);
 
        gbc.gridx = 2;
        panelFormulario.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 3;
        comboRol = new JComboBox<>(Rol.values());
        panelFormulario.add(comboRol, gbc);
 
        // Fila 2: estado activo/inactivo
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        chkActivo = new JCheckBox("Activo", true);
        panelFormulario.add(chkActivo, gbc);
 
        // Botones de accion
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Nuevo / Limpiar");
        btnGuardar = new JButton("Guardar");
        btnCambiarEstado = new JButton("Activar / Desactivar");
 
        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCambiarEstado);
 
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panelFormulario.add(panelBotones, gbc);
 
        add(panelFormulario, BorderLayout.NORTH);
 
        // Tabla de usuarios, solo lectura
        String[] columnas = {"ID", "Nombre", "Usuario", "Rol", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);
 
        // Listeners
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
 
        // Al seleccionar una fila, se carga ese usuario en el formulario
        // para poder editarlo (esto activa el modo "editar" en vez de "crear").
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                cargarUsuarioSeleccionado();
            }
        });
    }
 
    // Trae todos los usuarios reales desde AutenticacionServicio y llena la tabla.
    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        if (autenticacionServicio != null) {
            List<Usuario> usuarios = autenticacionServicio.listarUsuarios();
            if (usuarios != null) {
                for (Usuario u : usuarios) {
                    modeloTabla.addRow(new Object[]{
                            u.getId(),
                            u.getNombre(),
                            u.getUsuario(),
                            u.getRol(),
                            u.isActivo() ? "Activo" : "Inactivo"
                    });
                }
            }
        }
    }
 
    // Copia los datos de la fila seleccionada al formulario, para editarlos.
    // La contraseña se deja vacia a proposito: no se muestra ni se re-envia
    // si no se escribe una nueva (ver guardarUsuario/actualizarUsuario).
    private void cargarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila >= 0) {
            usuarioSeleccionadoId = (Integer) modeloTabla.getValueAt(fila, 0);
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
            txtUsuario.setText((String) modeloTabla.getValueAt(fila, 2));
            comboRol.setSelectedItem(modeloTabla.getValueAt(fila, 3));
            chkActivo.setSelected("Activo".equals(modeloTabla.getValueAt(fila, 4)));
            txtPassword.setText("");
        }
    }
 
    // Crea un usuario nuevo o actualiza el seleccionado, segun
    // usuarioSeleccionadoId. La contraseña que se escribe aqui va en texto
    // plano hasta que AutenticacionServicio.crearUsuario() la encripta con
    // SHA-256 antes de guardarla — este panel nunca guarda la contraseña
    // directo en la base de datos.
    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim();
        String usuarioStr = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        Rol rol = (Rol) comboRol.getSelectedItem();
        boolean activo = chkActivo.isSelected();
 
        if (nombre.isEmpty() || usuarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Usuario son campos obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        Usuario u = new Usuario(nombre, usuarioStr, password, rol);
        u.setActivo(activo);
 
        if (usuarioSeleccionadoId == null) {
            // Modo crear: la contraseña es obligatoria porque no hay una anterior
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es requerida para un nuevo usuario.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (autenticacionServicio != null) {
                autenticacionServicio.crearUsuario(u);
            }
            JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
        } else {
            u.setId(usuarioSeleccionadoId);
            if (autenticacionServicio != null) {
                autenticacionServicio.actualizarUsuario(u);
                // Solo si escribiste algo nuevo en el campo de contraseña
                if (!password.isEmpty()) {
                    autenticacionServicio.cambiarPassword(usuarioSeleccionadoId, password);
                }
            }
            JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente.");
        }
 
        limpiarFormulario();
        cargarUsuarios();
    }
 
    
    
    // Alterna activo/inactivo del usuario seleccionado en la tabla (no lo borra).
    private void cambiarEstado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        boolean estadoActual = "Activo".equals(modeloTabla.getValueAt(fila, 4));
        boolean nuevoEstado = !estadoActual;
 
        if (autenticacionServicio != null) {
            autenticacionServicio.cambiarEstadoUsuario(id, nuevoEstado);
        }
        JOptionPane.showMessageDialog(this, "Estado del usuario modificado a: " + (nuevoEstado ? "Activo" : "Inactivo"));
 
        limpiarFormulario();
        cargarUsuarios();
    }
 
    // Deja el formulario listo para crear un usuario nuevo (modo "crear").
    private void limpiarFormulario() {
        usuarioSeleccionadoId = null;
        txtNombre.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tablaUsuarios.clearSelection();
    }
}
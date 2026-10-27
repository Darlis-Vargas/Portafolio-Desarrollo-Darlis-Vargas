package com.taller.app.ui;

import com.taller.app.modelo.Cliente;
import com.taller.app.modelo.Vehiculo;
import com.taller.app.modelo.Servicio;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.OrdenServicio;
import com.taller.app.modelo.DetalleOrdenServicio;
import com.taller.app.modelo.DetalleOrdenProducto;
import com.taller.app.modelo.EstadoOrden;
import com.taller.app.modelo.Usuario;
import com.taller.app.modelo.Rol;
import com.taller.app.servicio.TallerServicio;
import com.taller.app.servicio.AutenticacionServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TallerPanel
 * Pantalla de Clientes, Vehiculos y Ordenes de Servicio del taller.
 * Paquete: com.taller.app.ui
 *
 * Conectada al backend real a traves de TallerServicio.
 */
public class TallerPanel extends JPanel {

    private final Usuario usuarioActual;
    private final TallerServicio tallerServicio;

    // Componentes de UI
    private DefaultTableModel modeloTablaClientes;
    private DefaultTableModel modeloTablaVehiculos;
    private DefaultTableModel modeloTablaHistorial;
    private DefaultTableModel modeloTablaServiciosOrden;
    private DefaultTableModel modeloTablaProductosOrden;

    private JComboBox<Cliente> comboClientesParaVehiculo;
    private JComboBox<Vehiculo> comboVehiculosHistorial;
    private JComboBox<Vehiculo> comboVehiculosNuevaOrden;
    private JComboBox<OrdenServicio> comboOrdenes;
    private JComboBox<Servicio> comboCatalogoServicios;
    private JComboBox<Producto> comboProductos;
    private JComboBox<Usuario> comboTecnicos;
    private JComboBox<EstadoOrden> comboProximoEstado;

    private JLabel lblInfoOrden;
    private JLabel lblTotalOrden;
    private JButton btnAsignarTecnico;
    private JButton btnCambiarEstado;
    private JButton btnRegistrarCliente;
    private JButton btnRegistrarVehiculo;
    private JButton btnCrearOrden;

    public TallerPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.tallerServicio = new TallerServicio();
        construirUI();
        refrescarTodo();
        aplicarRestriccionesPorRol();
    }

    // Construccion de la UI

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Taller - Clientes, Vehiculos y Ordenes de Servicio");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes y Vehiculos", crearPestanaClientesVehiculos());
        tabs.addTab("Historial / Nueva Orden", crearPestanaHistorialNuevaOrden());
        tabs.addTab("Detalle de Orden", crearPestanaDetalleOrden());
        add(tabs, BorderLayout.CENTER);
    }

    // Pestana 1: Clientes y Vehiculos

    private JPanel crearPestanaClientesVehiculos() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        panel.add(crearPanelRegistroCliente());
        panel.add(crearPanelRegistroVehiculo());

        return panel;
    }

    private JPanel crearPanelRegistroCliente() {
        JPanel contenedor = new JPanel(new BorderLayout(5, 5));
        contenedor.setBorder(BorderFactory.createTitledBorder("Registrar cliente nuevo"));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField txtNombre = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtDireccion = new JTextField();

        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Telefono:"));
        form.add(txtTelefono);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Direccion:"));
        form.add(txtDireccion);

        btnRegistrarCliente = new JButton("Registrar Cliente");
        btnRegistrarCliente.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);
            cliente.setDireccion(direccion);

            try {
                registrarClienteUI(cliente);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            txtNombre.setText("");
            txtTelefono.setText("");
            txtEmail.setText("");
            txtDireccion.setText("");

            JOptionPane.showMessageDialog(this, "Cliente registrado: " + cliente.getNombre());
        });

        modeloTablaClientes = new DefaultTableModel(new Object[]{"ID", "Nombre", "Telefono", "Email", "Direccion"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaClientes = new JTable(modeloTablaClientes);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(btnRegistrarCliente, BorderLayout.SOUTH);
        contenedor.add(norte, BorderLayout.NORTH);
        contenedor.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearPanelRegistroVehiculo() {
        JPanel contenedor = new JPanel(new BorderLayout(5, 5));
        contenedor.setBorder(BorderFactory.createTitledBorder("Registrar vehiculo nuevo"));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        comboClientesParaVehiculo = new JComboBox<>();
        comboClientesParaVehiculo.setRenderer(rendererCliente());

        JTextField txtPlaca = new JTextField();
        JTextField txtMarca = new JTextField();
        JTextField txtModelo = new JTextField();
        JTextField txtAnio = new JTextField();
        JTextField txtColor = new JTextField();

        form.add(new JLabel("Cliente:"));
        form.add(comboClientesParaVehiculo);
        form.add(new JLabel("Placa:"));
        form.add(txtPlaca);
        form.add(new JLabel("Marca:"));
        form.add(txtMarca);
        form.add(new JLabel("Modelo:"));
        form.add(txtModelo);
        form.add(new JLabel("Anio:"));
        form.add(txtAnio);
        form.add(new JLabel("Color:"));
        form.add(txtColor);

        btnRegistrarVehiculo = new JButton("Registrar Vehiculo");
        btnRegistrarVehiculo.addActionListener(e -> {
            Cliente clienteSel = (Cliente) comboClientesParaVehiculo.getSelectedItem();
            if (clienteSel == null) {
                JOptionPane.showMessageDialog(this, "Registra o selecciona un cliente primero.", "Falta cliente", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String placa = txtPlaca.getText().trim();
            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La placa es obligatoria.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int anio;
            try {
                anio = Integer.parseInt(txtAnio.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El anio debe ser un numero valido.", "Dato invalido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setIdCliente(clienteSel.getId());
            vehiculo.setPlaca(placa);
            vehiculo.setMarca(txtMarca.getText().trim());
            vehiculo.setModelo(txtModelo.getText().trim());
            vehiculo.setAnio(anio);
            vehiculo.setColor(txtColor.getText().trim());

            try {
                registrarVehiculoUI(vehiculo);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar vehiculo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            txtPlaca.setText("");
            txtMarca.setText("");
            txtModelo.setText("");
            txtAnio.setText("");
            txtColor.setText("");

            JOptionPane.showMessageDialog(this, "Vehiculo registrado: " + vehiculo.getPlaca());
        });

        modeloTablaVehiculos = new DefaultTableModel(new Object[]{"ID", "Placa", "Marca", "Modelo", "Anio", "Color", "Nombre Cliente"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaVehiculos = new JTable(modeloTablaVehiculos);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(btnRegistrarVehiculo, BorderLayout.SOUTH);

        contenedor.add(norte, BorderLayout.NORTH);
        contenedor.add(new JScrollPane(tablaVehiculos), BorderLayout.CENTER);

        return contenedor;
    }

    // Pestana 2: Historial y Nueva Orden

    private JPanel crearPestanaHistorialNuevaOrden() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboVehiculosHistorial = new JComboBox<>();
        comboVehiculosHistorial.setRenderer(rendererVehiculo());
        JButton btnVerHistorial = new JButton("Ver Historial");
        btnVerHistorial.addActionListener(e -> verHistorialUI());

        comboVehiculosNuevaOrden = new JComboBox<>();
        comboVehiculosNuevaOrden.setRenderer(rendererVehiculo());
        btnCrearOrden = new JButton("Crear Orden Nueva (estado inicial: RECIBIDO)");
        btnCrearOrden.addActionListener(e -> crearOrdenUI());

        controles.add(new JLabel("Vehiculo:"));
        controles.add(comboVehiculosHistorial);
        controles.add(btnVerHistorial);
        controles.add(Box.createHorizontalStrut(20));
        controles.add(new JLabel("Vehiculo para nueva orden:"));
        controles.add(comboVehiculosNuevaOrden);
        controles.add(btnCrearOrden);

        modeloTablaHistorial = new DefaultTableModel(
                new Object[]{"ID Orden", "Estado", "Fecha Ingreso", "Tecnico Asignado", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaHistorial = new JTable(modeloTablaHistorial);

        panel.add(controles, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
        return panel;
    }

    // Pestana 3: Detalle de Orden

    private JPanel crearPestanaDetalleOrden() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboOrdenes = new JComboBox<>();
        comboOrdenes.setRenderer(rendererOrden());
        comboOrdenes.addActionListener(e -> refrescarDetalleOrden());
        norte.add(new JLabel("Orden:"));
        norte.add(comboOrdenes);

        lblInfoOrden = new JLabel(" ");
        lblTotalOrden = new JLabel("Total: $0.00");
        lblTotalOrden.setFont(lblTotalOrden.getFont().deriveFont(Font.BOLD));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(lblInfoOrden);
        infoPanel.add(lblTotalOrden);

        comboCatalogoServicios = new JComboBox<>();
        comboCatalogoServicios.setRenderer(rendererServicio());
        JSpinner spinnerCantidadServicio = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton btnAgregarServicio = new JButton("Agregar servicio");
        btnAgregarServicio.addActionListener(e -> agregarServicioUI((int) spinnerCantidadServicio.getValue()));

        JPanel panelServicioForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelServicioForm.add(new JLabel("Servicio:"));
        panelServicioForm.add(comboCatalogoServicios);
        panelServicioForm.add(new JLabel("Cant.:"));
        panelServicioForm.add(spinnerCantidadServicio);
        panelServicioForm.add(btnAgregarServicio);

        modeloTablaServiciosOrden = new DefaultTableModel(new Object[]{"Servicio", "Cantidad", "Precio Base", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaServiciosOrden = new JTable(modeloTablaServiciosOrden);

        JPanel panelServicios = new JPanel(new BorderLayout());
        panelServicios.setBorder(BorderFactory.createTitledBorder("Servicios agregados"));
        panelServicios.add(panelServicioForm, BorderLayout.NORTH);
        panelServicios.add(new JScrollPane(tablaServiciosOrden), BorderLayout.CENTER);

        comboProductos = new JComboBox<>();
        comboProductos.setRenderer(rendererProducto());
        JSpinner spinnerCantidadProducto = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        JButton btnAgregarProducto = new JButton("Agregar producto (descuenta stock)");
        btnAgregarProducto.addActionListener(e -> agregarProductoUI((int) spinnerCantidadProducto.getValue()));

        JPanel panelProductoForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelProductoForm.add(new JLabel("Producto:"));
        panelProductoForm.add(comboProductos);
        panelProductoForm.add(new JLabel("Cant.:"));
        panelProductoForm.add(spinnerCantidadProducto);
        panelProductoForm.add(btnAgregarProducto);

        modeloTablaProductosOrden = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Precio Venta", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaProductosOrden = new JTable(modeloTablaProductosOrden);

        JPanel panelProductos = new JPanel(new BorderLayout());
        panelProductos.setBorder(BorderFactory.createTitledBorder("Productos / insumos usados"));
        panelProductos.add(panelProductoForm, BorderLayout.NORTH);
        panelProductos.add(new JScrollPane(tablaProductosOrden), BorderLayout.CENTER);

        JPanel panelListas = new JPanel(new GridLayout(1, 2, 10, 10));
        panelListas.add(panelServicios);
        panelListas.add(panelProductos);

        comboProximoEstado = new JComboBox<>();
        btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.addActionListener(e -> cambiarEstadoUI());

        comboTecnicos = new JComboBox<>();
        comboTecnicos.setRenderer(rendererUsuario());
        btnAsignarTecnico = new JButton("Asignar Tecnico");
        btnAsignarTecnico.addActionListener(e -> asignarTecnicoUI());

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.add(new JLabel("Siguiente estado:"));
        panelAcciones.add(comboProximoEstado);
        panelAcciones.add(btnCambiarEstado);
        panelAcciones.add(Box.createHorizontalStrut(20));
        panelAcciones.add(new JLabel("Tecnico:"));
        panelAcciones.add(comboTecnicos);
        panelAcciones.add(btnAsignarTecnico);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(infoPanel, BorderLayout.NORTH);
        sur.add(panelAcciones, BorderLayout.SOUTH);

        panel.add(norte, BorderLayout.NORTH);
        panel.add(panelListas, BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);

        return panel;
    }

    // Acciones: todas conectan directo con TallerServicio

    private void registrarClienteUI(Cliente cliente) {
        tallerServicio.registrarCliente(cliente);
        refrescarTablaClientes();
        refrescarComboClientes();
    }

    private void registrarVehiculoUI(Vehiculo vehiculo) {
        tallerServicio.registrarVehiculo(vehiculo);
        refrescarTablaVehiculos();
        refrescarCombosVehiculos();
    }

    private void verHistorialUI() {
        Vehiculo vehiculo = (Vehiculo) comboVehiculosHistorial.getSelectedItem();
        modeloTablaHistorial.setRowCount(0);
        if (vehiculo == null) {
            return;
        }

        List<OrdenServicio> historial = tallerServicio.historialVehiculo(vehiculo.getId());

        for (OrdenServicio o : historial) {
            modeloTablaHistorial.addRow(new Object[]{
                    o.getId(),
                    o.getEstado(),
                    o.getFechaIngreso(),
                    nombreTecnicoDeOrden(o),
                    String.format("$%.2f", o.getTotal())
            });
        }
    }

    private void crearOrdenUI() {
        Vehiculo vehiculo = (Vehiculo) comboVehiculosNuevaOrden.getSelectedItem();
        if (vehiculo == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un vehiculo primero.", "Falta vehiculo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente cliente = buscarClientePorId(vehiculo.getIdCliente());
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "No se encontro el cliente dueno del vehiculo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        OrdenServicio orden;
        try {
            orden = tallerServicio.crearOrden(vehiculo, cliente, usuarioActual);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear la orden: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarComboOrdenes();
        comboOrdenes.setSelectedItem(orden);
        JOptionPane.showMessageDialog(this, "Orden creada con estado RECIBIDO.");
    }

    private void agregarServicioUI(int cantidad) {
        OrdenServicio orden = (OrdenServicio) comboOrdenes.getSelectedItem();
        Servicio servicio = (Servicio) comboCatalogoServicios.getSelectedItem();
        if (!validarAccionSobreOrden(orden)) {
            return;
        }
        if (servicio == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un servicio del catalogo.", "Falta servicio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            tallerServicio.agregarServicio(orden, servicio, cantidad);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar servicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarDetalleOrden();
    }

    private void agregarProductoUI(int cantidad) {
        OrdenServicio orden = (OrdenServicio) comboOrdenes.getSelectedItem();
        Producto producto = (Producto) comboProductos.getSelectedItem();
        if (!validarAccionSobreOrden(orden)) {
            return;
        }
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto.", "Falta producto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // agregarProducto puede lanzar excepcion si no hay stock suficiente en Taller
            tallerServicio.agregarProducto(orden, producto, cantidad);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarDetalleOrden();
    }

    private void cambiarEstadoUI() {
        OrdenServicio orden = (OrdenServicio) comboOrdenes.getSelectedItem();
        EstadoOrden nuevoEstado = (EstadoOrden) comboProximoEstado.getSelectedItem();
        if (!validarAccionSobreOrden(orden)) {
            return;
        }
        if (nuevoEstado == null) {
            return;
        }

        try {
            // cambiarEstado puede lanzar excepcion si se intenta saltar un paso
            tallerServicio.cambiarEstado(orden, nuevoEstado);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarComboOrdenes();
        comboOrdenes.setSelectedItem(orden);
        refrescarDetalleOrden();
    }

    private void asignarTecnicoUI() {
        OrdenServicio orden = (OrdenServicio) comboOrdenes.getSelectedItem();
        Usuario tecnico = (Usuario) comboTecnicos.getSelectedItem();
        if (orden == null || tecnico == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una orden y un tecnico.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            tallerServicio.asignarTecnico(orden, tecnico);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al asignar tecnico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refrescarComboOrdenes();
        comboOrdenes.setSelectedItem(orden);
        refrescarDetalleOrden();
    }

    /**
     * Valida que la orden exista y que, si el usuario actual es TECNICO,
     * la orden sea suya.
     */
    private boolean validarAccionSobreOrden(OrdenServicio orden) {
        if (orden == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una orden primero.", "Falta orden", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (esTecnico(usuarioActual) && orden.getIdUsuarioTecnico() != usuarioActual.getId()) {
            JOptionPane.showMessageDialog(this,
                    "Como Tecnico solo puedes actuar sobre tus propias ordenes asignadas.",
                    "Accion no permitida", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // Roles

    private boolean esTecnico(Usuario u) {
        return u != null && u.getRol() != null && "TECNICO".equalsIgnoreCase(u.getRol().toString());
    }

    private void aplicarRestriccionesPorRol() {
        boolean tecnico = esTecnico(usuarioActual);

        // Un Tecnico no crea clientes/vehiculos ni ordenes nuevas, ni asigna
        // tecnicos: solo actualiza el estado de sus propias ordenes.
        btnRegistrarCliente.setEnabled(!tecnico);
        btnRegistrarVehiculo.setEnabled(!tecnico);
        btnCrearOrden.setEnabled(!tecnico);
        btnAsignarTecnico.setEnabled(!tecnico);

        refrescarComboOrdenes();
    }

    // Solo se necesitan los usuarios con rol TECNICO para el combo de asignar
    private List<Usuario> obtenerTecnicos() {
        List<Usuario> tecnicos = new ArrayList<>();
        for (Usuario u : new AutenticacionServicio().listarUsuarios()) {
            if (u.getRol() == Rol.TECNICO) {
                tecnicos.add(u);
            }
        }
        return tecnicos;
    }

    // Refresco de tablas / combos

    private void refrescarTodo() {
        refrescarTablaClientes();
        refrescarComboClientes();
        refrescarTablaVehiculos();
        refrescarCombosVehiculos();
        refrescarCombosCatalogo();
        refrescarComboTecnicos();
        refrescarComboOrdenes();
    }

    private void refrescarTablaClientes() {
        modeloTablaClientes.setRowCount(0);
        for (Cliente c : tallerServicio.listarClientes()) {
            modeloTablaClientes.addRow(new Object[]{c.getId(), c.getNombre(), c.getTelefono(), c.getEmail(), c.getDireccion()});
        }
    }

    private void refrescarComboClientes() {
        comboClientesParaVehiculo.removeAllItems();
        for (Cliente c : tallerServicio.listarClientes()) {
            comboClientesParaVehiculo.addItem(c);
        }
    }

    private void refrescarTablaVehiculos() {
        modeloTablaVehiculos.setRowCount(0);
        for (Vehiculo v : tallerServicio.listarVehiculos()) {
            Cliente dueno = buscarClientePorId(v.getIdCliente());
            String nombreDueno = (dueno != null) ? dueno.getNombre() : "(desconocido)";
            modeloTablaVehiculos.addRow(new Object[]{v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnio(), v.getColor(), nombreDueno});
        }
    }

    private void refrescarCombosVehiculos() {
        comboVehiculosHistorial.removeAllItems();
        comboVehiculosNuevaOrden.removeAllItems();
        for (Vehiculo v : tallerServicio.listarVehiculos()) {
            comboVehiculosHistorial.addItem(v);
            comboVehiculosNuevaOrden.addItem(v);
        }
    }

    private void refrescarCombosCatalogo() {
        comboCatalogoServicios.removeAllItems();
        for (Servicio s : tallerServicio.listarCatalogoServicios()) {
            comboCatalogoServicios.addItem(s);
        }
        comboProductos.removeAllItems();
        for (Producto p : tallerServicio.listarProductosDisponibles()) {
            comboProductos.addItem(p);
        }
    }

    private void refrescarComboTecnicos() {
        comboTecnicos.removeAllItems();
        for (Usuario u : obtenerTecnicos()) {
            comboTecnicos.addItem(u);
        }
    }

    private void refrescarComboOrdenes() {
        Object seleccionActual = comboOrdenes.getSelectedItem();
        comboOrdenes.removeAllItems();
        boolean tecnico = esTecnico(usuarioActual);
        for (OrdenServicio o : tallerServicio.listarOrdenes()) {
            if (!tecnico || o.getIdUsuarioTecnico() == usuarioActual.getId()) {
                comboOrdenes.addItem(o);
            }
        }
        if (seleccionActual != null) {
            comboOrdenes.setSelectedItem(seleccionActual);
        }
        refrescarDetalleOrden();
    }

    private void refrescarDetalleOrden() {
        OrdenServicio orden = (OrdenServicio) comboOrdenes.getSelectedItem();
        modeloTablaServiciosOrden.setRowCount(0);
        modeloTablaProductosOrden.setRowCount(0);

        if (orden == null) {
            lblInfoOrden.setText("Selecciona una orden.");
            lblTotalOrden.setText("Total: $0.00");
            comboProximoEstado.removeAllItems();
            return;
        }

        Vehiculo vehiculo = buscarVehiculoPorId(orden.getIdVehiculo());
        String placa = vehiculo != null ? vehiculo.getPlaca() : "?";
        lblInfoOrden.setText(String.format("Orden #%d | Vehiculo: %s | Estado: %s | Tecnico: %s",
                orden.getId(), placa, orden.getEstado(), nombreTecnicoDeOrden(orden)));
        lblTotalOrden.setText(String.format("Total: $%.2f", orden.getTotal()));

        // Las lineas ya se guardaron con su precio y subtotal al momento de
        // agregarlas, asi que se muestran tal cual, sin recalcular.
        for (DetalleOrdenServicio d : tallerServicio.listarServiciosDeOrden(orden)) {
            modeloTablaServiciosOrden.addRow(new Object[]{
                    buscarNombreServicio(d.getIdServicio()), d.getCantidad(),
                    String.format("$%.2f", d.getPrecioUnitario()),
                    String.format("$%.2f", d.getSubtotal())
            });
        }
        for (DetalleOrdenProducto d : tallerServicio.listarProductosDeOrden(orden)) {
            modeloTablaProductosOrden.addRow(new Object[]{
                    buscarNombreProducto(d.getIdProducto()), d.getCantidad(),
                    String.format("$%.2f", d.getPrecioUnitario()),
                    String.format("$%.2f", d.getSubtotal())
            });
        }

        comboProximoEstado.removeAllItems();
        EstadoOrden siguiente = siguienteEstado(orden.getEstado());
        if (siguiente != null) {
            comboProximoEstado.addItem(siguiente);
        }

        boolean puedeActuar = !esTecnico(usuarioActual) || orden.getIdUsuarioTecnico() == usuarioActual.getId();
        btnCambiarEstado.setEnabled(puedeActuar && siguiente != null);
    }

    /**
     * Devuelve el siguiente estado valido segun el orden fijo
     * RECIBIDO -> EN_PROCESO -> TERMINADO -> ENTREGADO, o null si ya
     * esta en el ultimo estado (no se permite saltar pasos).
     */
    private EstadoOrden siguienteEstado(EstadoOrden actual) {
        EstadoOrden[] secuencia = {EstadoOrden.RECIBIDO, EstadoOrden.EN_PROCESO, EstadoOrden.TERMINADO, EstadoOrden.ENTREGADO};
        for (int i = 0; i < secuencia.length - 1; i++) {
            if (secuencia[i] == actual) {
                return secuencia[i + 1];
            }
        }
        return null;
    }

    // Renderers: para que los combos muestren texto legible en vez de toString()

    private javax.swing.ListCellRenderer<Object> rendererCliente() {
        return crearRenderer(item -> item instanceof Cliente ? ((Cliente) item).getNombre() : "");
    }

    private javax.swing.ListCellRenderer<Object> rendererVehiculo() {
        return crearRenderer(item -> {
            if (!(item instanceof Vehiculo)) return "";
            Vehiculo v = (Vehiculo) item;
            return v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo();
        });
    }

    private javax.swing.ListCellRenderer<Object> rendererOrden() {
        return crearRenderer(item -> {
            if (!(item instanceof OrdenServicio)) return "";
            OrdenServicio o = (OrdenServicio) item;
            return "Orden #" + o.getId() + " (" + o.getEstado() + ")";
        });
    }

    private javax.swing.ListCellRenderer<Object> rendererServicio() {
        return crearRenderer(item -> {
            if (!(item instanceof Servicio)) return "";
            Servicio s = (Servicio) item;
            return s.getNombre() + String.format(" ($%.2f)", s.getPrecioBase());
        });
    }

    private javax.swing.ListCellRenderer<Object> rendererProducto() {
        return crearRenderer(item -> {
            if (!(item instanceof Producto)) return "";
            Producto p = (Producto) item;
            return p.getNombre() + String.format(" ($%.2f)", p.getPrecioVenta());
        });
    }

    private javax.swing.ListCellRenderer<Object> rendererUsuario() {
        return crearRenderer(item -> item instanceof Usuario ? ((Usuario) item).getNombre() : "");
    }

    private javax.swing.ListCellRenderer<Object> crearRenderer(java.util.function.Function<Object, String> texto) {
        return new javax.swing.DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(texto.apply(value));
                return this;
            }
        };
    }

    // Busquedas auxiliares

    private Cliente buscarClientePorId(int id) {
        for (Cliente c : tallerServicio.listarClientes()) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    private Vehiculo buscarVehiculoPorId(int id) {
        for (Vehiculo v : tallerServicio.listarVehiculos()) {
            if (v.getId() == id) return v;
        }
        return null;
    }

    private String nombreTecnicoDeOrden(OrdenServicio orden) {
        for (Usuario u : obtenerTecnicos()) {
            if (u.getId() == orden.getIdUsuarioTecnico()) {
                return u.getNombre();
            }
        }
        return "(sin asignar)";
    }

    // Se usan para mostrar el nombre en la tabla de detalle (DetalleOrdenServicio
    // y DetalleOrdenProducto solo guardan el id, no el objeto completo)

    private String buscarNombreServicio(int idServicio) {
        for (Servicio s : tallerServicio.listarCatalogoServicios()) {
            if (s.getId() == idServicio) return s.getNombre();
        }
        return "(servicio #" + idServicio + ")";
    }

    private String buscarNombreProducto(int idProducto) {
        for (Producto p : tallerServicio.listarProductosDisponibles()) {
            if (p.getId() == idProducto) return p.getNombre();
        }
        return "(producto #" + idProducto + ")";
    }
}

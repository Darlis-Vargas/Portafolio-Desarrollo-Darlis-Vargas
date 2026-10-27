package com.taller.app.ui;

import com.taller.app.modelo.EstadoOrden;
import com.taller.app.modelo.OrdenServicio;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.Rol;
import com.taller.app.modelo.Venta;
import com.taller.app.servicio.ReportesServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * ReportesPanel
 * Pantalla de reportes: ventas por fecha, stock bajo, ordenes por estado.
 * Paquete: com.taller.app.ui
 *
 * Que pestanas se muestran depende del rol logueado (se decide aqui mismo,
 * no en MenuPrincipalFrame, porque cada rol ve un subconjunto distinto).
 */
public class ReportesPanel extends JPanel {

    private final ReportesServicio reportesServicio;
    private final Rol rolUsuario;

    private JTabbedPane tabbedPane;

    // Ventas
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;

    // Stock Bajo
    private JTable tablaStock;
    private DefaultTableModel modeloStock;

    // Ordenes por Estado
    private JComboBox<EstadoOrden> comboEstado;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloOrdenes;

    public ReportesPanel(ReportesServicio reportesServicio, Rol rolUsuario) {
        this.reportesServicio = reportesServicio;
        this.rolUsuario = rolUsuario;

        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        construirPestaniasSegunRol();
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Arma solo las pestanas que le corresponden al rol logueado.
    // Administrador ve las 3, Cajero solo Ventas, Recepcion de Taller solo
    // Ordenes. Cualquier otro rol (por ejemplo Tecnico) no tiene reportes
    // definidos, asi que se le avisa con un dialogo en vez de dejarlo con
    // una pantalla en blanco.
    private void construirPestaniasSegunRol() {
        if (rolUsuario == Rol.ADMINISTRADOR) {
            tabbedPane.addTab("Ventas por Fecha", crearPanelVentas());
            tabbedPane.addTab("Stock Bajo", crearPanelStockBajo());
            tabbedPane.addTab("Órdenes por Estado", crearPanelOrdenes());
        } else if (rolUsuario == Rol.CAJERO) {
            tabbedPane.addTab("Ventas por Fecha", crearPanelVentas());
        } else if (rolUsuario == Rol.RECEPCION_TALLER) {
            tabbedPane.addTab("Órdenes por Estado", crearPanelOrdenes());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tu rol no tiene acceso a los reportes.",
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            tabbedPane.addTab("Sin acceso", crearPanelSinAcceso());
        }
    }

    // Pantalla simple que se muestra en vez de las pestanas normales cuando
    // el rol no tiene ningun reporte asignado.
    private JPanel crearPanelSinAcceso() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("No tienes permiso para ver reportes.", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(new JLabel("Desde (YYYY-MM-DD):"));
        txtFechaDesde = new JTextField(LocalDate.now().minusDays(30).toString(), 10);
        panelFiltros.add(txtFechaDesde);

        panelFiltros.add(new JLabel("Hasta (YYYY-MM-DD):"));
        txtFechaHasta = new JTextField(LocalDate.now().toString(), 10);
        panelFiltros.add(txtFechaHasta);

        JButton btnBuscar = new JButton("Buscar");
        panelFiltros.add(btnBuscar);
        panel.add(panelFiltros, BorderLayout.NORTH);

        String[] cols = {"ID", "ID Cajero", "ID Cliente", "Fecha", "Total", "Método Pago"};
        modeloVentas = new DefaultTableModel(cols, 0);
        tablaVentas = new JTable(modeloVentas);
        panel.add(new JScrollPane(tablaVentas), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscarVentas());

        return panel;
    }

    private void buscarVentas() {
        try {
            LocalDate desde = LocalDate.parse(txtFechaDesde.getText().trim());
            LocalDate hasta = LocalDate.parse(txtFechaHasta.getText().trim());

            modeloVentas.setRowCount(0);
            if (reportesServicio != null) {
                List<Venta> ventas = reportesServicio.reporteVentas(desde, hasta);
                if (ventas != null) {
                    for (Venta v : ventas) {
                        modeloVentas.addRow(new Object[]{
                                v.getId(),
                                v.getIdUsuarioCajero(),
                                v.getIdCliente(),
                                v.getFecha(),
                                v.getTotal(),
                                v.getMetodoPago()
                        });
                    }
                }
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelStockBajo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar Productos con Stock Bajo");
        panelSuperior.add(btnCargar);
        panel.add(panelSuperior, BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre", "Precio Venta", "Stock Mínimo"};
        modeloStock = new DefaultTableModel(cols, 0);
        tablaStock = new JTable(modeloStock);
        panel.add(new JScrollPane(tablaStock), BorderLayout.CENTER);

        btnCargar.addActionListener(e -> cargarStockBajo());

        return panel;
    }

    private void cargarStockBajo() {
        modeloStock.setRowCount(0);
        if (reportesServicio != null) {
            List<Producto> productos = reportesServicio.reporteStockBajo();
            if (productos != null) {
                for (Producto p : productos) {
                    modeloStock.addRow(new Object[]{
                            p.getId(),
                            p.getNombre(),
                            p.getPrecioVenta(),
                            p.getStockMinimo()
                    });
                }
            }
        }
    }

    private JPanel crearPanelOrdenes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(EstadoOrden.values());
        panelFiltros.add(comboEstado);

        JButton btnBuscar = new JButton("Buscar");
        panelFiltros.add(btnBuscar);
        panel.add(panelFiltros, BorderLayout.NORTH);

        String[] cols = {"ID", "ID Vehículo", "ID Cliente", "Estado", "Total", "Fechas"};
        modeloOrdenes = new DefaultTableModel(cols, 0);
        tablaOrdenes = new JTable(modeloOrdenes);
        panel.add(new JScrollPane(tablaOrdenes), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscarOrdenes());

        return panel;
    }

    private void buscarOrdenes() {
        EstadoOrden estado = (EstadoOrden) comboEstado.getSelectedItem();
        modeloOrdenes.setRowCount(0);

        if (reportesServicio != null) {
            List<OrdenServicio> ordenes = reportesServicio.reporteOrdenesPorEstado(estado);
            if (ordenes != null) {
                for (OrdenServicio o : ordenes) {
                    // OrdenServicio no tiene un "getFechas()" unico; se arma
                    // un texto juntando las 3 fechas que si existen.
                    String fechas = "Ingreso: " + o.getFechaIngreso()
                            + (o.getFechaEntregaEstimada() != null ? " | Est: " + o.getFechaEntregaEstimada() : "")
                            + (o.getFechaEntregaReal() != null ? " | Real: " + o.getFechaEntregaReal() : "");
                    modeloOrdenes.addRow(new Object[]{
                            o.getId(),
                            o.getIdVehiculo(),
                            o.getIdCliente(),
                            o.getEstado(),
                            o.getTotal(),
                            fechas
                    });
                }
            }
        }
    }
}


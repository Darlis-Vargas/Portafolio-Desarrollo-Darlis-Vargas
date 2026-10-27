package com.taller.app.ui;
 
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.Venta;
import com.taller.app.servicio.VentaServicio;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
 
/**
 * VentasPanel
 * Pantalla de ventas en tienda: buscar producto, armar un carrito, cobrar.
 * Paquete: com.taller.app.ui
 *
 * Conectada directo al backend real (VentaServicio) desde el principio,
 * no maneja modo de datos de prueba como los otros paneles.
 */
public class VentasPanel extends JPanel {
 
	private static final long serialVersionUID = 1L;
 
	private JTextField textBuscador;
	private JButton btnBuscar, btnMostrarTodo, btnAgregar, btnEliminarCarrito, btnCobrar;
	private JTable tableProductos, tableCarrito;
	private JLabel lblTotalDinero;
	private DefaultTableModel modeloTablaProductos, modeloTablaCarrito;
	private JSpinner spinnerCantidad;
	private JComboBox<String> comboBoxMetodoPago;
	private VentaServicio ventaServicio;
 
	// La venta que se esta armando en pantalla ahora mismo (el "carrito").
	// Se reinicia cada vez que se cobra o se abre el panel.
	private Venta ventaActual;
 
	// Copia en memoria de los productos que trajo el backend, para poder
	// filtrar la tabla sin volver a consultar la base de datos cada vez.
	private List<Producto> listaProductosDisponibles;
 
	public VentasPanel() {
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(156, 43, 39));
		this.ventaServicio = new VentaServicio();
		setLayout(new GridLayout(0, 2, 10, 0));
 
		// ---------------- Panel izquierdo: busqueda de productos ----------------
		JPanel panelIzquierdo = new JPanel();
		panelIzquierdo.setBorder(new TitledBorder(new LineBorder(new Color(176, 58, 54)), "Busqueda de Productos ", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
		panelIzquierdo.setToolTipText("");
		panelIzquierdo.setBackground(new Color(176, 58, 54));
        panelIzquierdo.setLayout(new BorderLayout(5, 5));
        add(panelIzquierdo);
 
        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(176, 58, 54));
        panelTop.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelIzquierdo.add(panelTop, BorderLayout.NORTH);
 
        textBuscador = new JTextField();
        panelTop.add(textBuscador);
        textBuscador.setColumns(15);
 
        btnBuscar = new JButton("Buscar");
        panelTop.add(btnBuscar);
 
        btnMostrarTodo = new JButton("Mostrar Todo");
        panelTop.add(btnMostrarTodo);
 
        JScrollPane scrollPaneProductos = new JScrollPane();
        panelIzquierdo.add(scrollPaneProductos, BorderLayout.CENTER);
 
        // Tabla de productos: solo lectura, no se edita escribiendo en las celdas
        String[] columnasProd = {"ID", "Nombre", "Descripción", "Precio Venta"};
        modeloTablaProductos = new DefaultTableModel(columnasProd, 0) {
        	@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProductos = new JTable(modeloTablaProductos);
        scrollPaneProductos.setViewportView(tableProductos);
 
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(new Color(176, 58, 54));
        panelInferior.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panelIzquierdo.add(panelInferior, BorderLayout.SOUTH);
 
        JLabel lblTallerTitulo = new JLabel("Taller ITLA");
        lblTallerTitulo.setVerticalAlignment(SwingConstants.BOTTOM);
        lblTallerTitulo.setForeground(new Color(255, 255, 255));
        lblTallerTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        panelInferior.add(lblTallerTitulo);
 
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblCantidad.setForeground(new Color(255, 255, 255));
        panelInferior.add(lblCantidad);
 
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerCantidad.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panelInferior.add(spinnerCantidad);
 
        btnAgregar = new JButton("Agregar al Carrito");
        panelInferior.add(btnAgregar);
 
        // ---------------- Panel derecho: carrito de compras ----------------
        JPanel panelDerecho = new JPanel();
        panelDerecho.setBorder(new TitledBorder(new LineBorder(new Color(176, 58, 54)), "Carrito de Compras", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
        panelDerecho.setBackground(new Color(176, 58, 54));
        panelDerecho.setLayout(new BorderLayout(5, 5));
        add(panelDerecho);
 
        JScrollPane scrollPaneCarrito = new JScrollPane();
        panelDerecho.add(scrollPaneCarrito, BorderLayout.CENTER);
 
        String[] columnasCarrito = {"Producto", "Precio Unitario", "Cantidad", "Subtotal"};
        modeloTablaCarrito = new DefaultTableModel(columnasCarrito, 0) {
        	@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCarrito = new JTable(modeloTablaCarrito);
        scrollPaneCarrito.setViewportView(tableCarrito);
 
        JPanel panelRight = new JPanel();
        panelRight.setBackground(new Color(176, 58, 54));
        panelRight.setLayout(new BorderLayout(5, 5));
        panelDerecho.add(panelRight, BorderLayout.SOUTH);
 
        btnEliminarCarrito = new JButton("Quitar Selección");
        panelRight.add(btnEliminarCarrito, BorderLayout.NORTH);
 
        // Metodo de pago, total y boton de cobrar
        JPanel panelCobro = new JPanel();
        panelCobro.setBackground(new Color(176, 58, 54));
        panelCobro.setLayout(new GridLayout(3, 2, 5, 5));
        panelRight.add(panelCobro, BorderLayout.SOUTH);
 
        JLabel lblMetodo = new JLabel("Método de Pago:");
        lblMetodo.setForeground(new Color(255, 255, 255));
        lblMetodo.setFont(new Font("Tahoma", Font.BOLD, 12));
        panelCobro.add(lblMetodo);
 
        comboBoxMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        comboBoxMetodoPago.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panelCobro.add(comboBoxMetodoPago);
 
        JLabel lblTotalPago = new JLabel("TOTAL:");
        lblTotalPago.setForeground(new Color(255, 255, 255));
        lblTotalPago.setFont(new Font("Tahoma", Font.BOLD, 14));
        panelCobro.add(lblTotalPago);
 
        lblTotalDinero = new JLabel("RD$ 0,00");
        lblTotalDinero.setForeground(new Color(255, 215, 0));
        lblTotalDinero.setFont(new Font("Tahoma", Font.BOLD, 17));
        panelCobro.add(lblTotalDinero);
 
        panelCobro.add(new JLabel());
 
        btnCobrar = new JButton("Cobrar Venta");
        panelCobro.add(btnCobrar);
 
        // Estado inicial: carrito vacio y tabla de productos cargada desde la BD
        iniciarNuevaVenta();
        cargarTablaProductos();
 
        // ---------------- Eventos ----------------
        btnBuscar.addActionListener(e -> filtrarProductos());
        btnMostrarTodo.addActionListener(e -> {
            textBuscador.setText("");
            refrescarTablaProductos(listaProductosDisponibles);
        });
 
        // Permite buscar tambien presionando Enter en el campo de texto
        textBuscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filtrarProductos();
                }
            }
        });
 
        btnAgregar.addActionListener(e -> agregarProductoAlCarrito());
        btnEliminarCarrito.addActionListener(e -> quitarDelCarrito());
        btnCobrar.addActionListener(e -> procesarCobro());
	}
 
	// Crea una Venta vacia nueva y limpia la tabla del carrito.
	// Se llama al abrir el panel y despues de cada cobro exitoso.
	private void iniciarNuevaVenta() {
        ventaActual = new Venta();
        modeloTablaCarrito.setRowCount(0);
        actualizarTotal();
	}
 
	// Trae los productos disponibles desde el backend real.
	private void cargarTablaProductos() {
        try {
            listaProductosDisponibles = ventaServicio.listarProductosDisponibles();
        } catch (Exception e) {
            listaProductosDisponibles = new ArrayList<>();
        }
        refrescarTablaProductos(listaProductosDisponibles);
    }
 
	// Vuelve a pintar la tabla de productos con la lista que se le pase
	// (la lista completa, o una lista ya filtrada por texto de busqueda).
    private void refrescarTablaProductos(List<Producto> lista) {
        modeloTablaProductos.setRowCount(0);
        if (lista != null) {
            for (Producto p : lista) {
                Object[] fila = {
                    p.getId(),
                    p.getNombre(),
                    p.getDescripcion(),
                    String.format("RD$ %.2f", p.getPrecioVenta())
                };
                modeloTablaProductos.addRow(fila);
            }
        }
    }
 
    // Filtra la lista de productos en memoria por id, nombre o descripcion
    // (sin volver a consultar la base de datos).
    private void filtrarProductos() {
        String texto = textBuscador.getText().toLowerCase().trim();
        List<Producto> filtrada = new ArrayList<>();
 
        if (listaProductosDisponibles != null) {
            for (Producto p : listaProductosDisponibles) {
                String idStr = String.valueOf(p.getId());
                String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
                String desc = p.getDescripcion() != null ? p.getDescripcion().toLowerCase() : "";
                if (idStr.contains(texto) || nombre.contains(texto) || desc.contains(texto)) {
                    filtrada.add(p);
                }
            }
        }
        refrescarTablaProductos(filtrada);
    }
 
    // Toma el producto seleccionado en la tabla de arriba y lo agrega al
    // carrito con la cantidad del spinner. VentaServicio ya valida que haya
    // stock suficiente en Tienda antes de dejarlo agregar.
    private void agregarProductoAlCarrito() {
        int fila = tableProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        int id = (int) modeloTablaProductos.getValueAt(fila, 0);
        Producto seleccionado = null;
        for (Producto p : listaProductosDisponibles) {
            if (p.getId() == id) {
                seleccionado = p;
                break;
            }
        }
 
        if (seleccionado != null) {
            int cantidad = (int) spinnerCantidad.getValue();
            try {
                // Esto solo agrega la linea en memoria (venta.getItems()) y
                // valida stock; el descuento real de stock pasa al cobrar.
                ventaServicio.agregarProducto(ventaActual, seleccionado, cantidad);
 
                double subtotal = seleccionado.getPrecioVenta() * cantidad;
                Object[] filaCarrito = {
                    seleccionado.getNombre(),
                    String.format("RD$ %.2f", seleccionado.getPrecioVenta()),
                    cantidad,
                    String.format("RD$ %.2f", subtotal)
                };
                modeloTablaCarrito.addRow(filaCarrito);
                actualizarTotal();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente o error: " + ex.getMessage(), "Error de Stock", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    // Quita la fila seleccionada del carrito (solo de la tabla; ya que
    // VentaServicio.agregarProducto no tiene un "quitar", esto solo afecta
    // lo que se ve en pantalla hasta que se recalcula el total).
    private void quitarDelCarrito() {
        int fila = tableCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un elemento del carrito.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloTablaCarrito.removeRow(fila);
        actualizarTotal();
    }
 
    // Le pide a VentaServicio que sume todas las lineas ya agregadas.
    private void actualizarTotal() {
        double total = ventaServicio.calcularTotal(ventaActual);
        lblTotalDinero.setText(String.format("RD$ %.2f", total));
    }
 
    // Cobra la venta: guarda la venta y su detalle en la base de datos, y
    // descuenta el stock de Tienda de cada producto vendido. El cajero se
    // asigna solo, dentro de VentaServicio.registrar(), usando la sesion activa.
    private void procesarCobro() {
        if (modeloTablaCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        try {
            String metodo = (String) comboBoxMetodoPago.getSelectedItem();
            ventaActual.setMetodoPago(metodo);
            ventaServicio.registrar(ventaActual);
            JOptionPane.showMessageDialog(this, "¡Venta registrada con éxito y stock descontado!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
 
            // Listo para la siguiente venta, y refresca stock por si algun
            // producto quedo por debajo de lo que se mostraba antes.
            iniciarNuevaVenta();
            cargarTablaProductos();
            textBuscador.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cobrar la venta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
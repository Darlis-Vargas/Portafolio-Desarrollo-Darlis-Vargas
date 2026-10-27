package com.taller.app.ui;

import com.taller.app.modelo.Producto;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.modelo.Rol;
import com.taller.app.modelo.Usuario;
import com.taller.app.servicio.InventarioServicio;
import com.taller.app.servicio.Sesion;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de gestion de inventario para el sistema del taller y tienda.
 *
 * Que hace esta clase, en resumen:
 * 1) Muestra una tabla con todos los productos y su stock.
 * 2) Oculta la columna de Tienda o Taller segun el rol del usuario logueado.
 * 3) Permite mover stock entre Tienda y Taller (con el origen bloqueado
 *    si el rol solo administra una de las dos ubicaciones).
 * 4) Permite crear y editar productos.
 */
public class InventarioPanel extends JPanel {

    // Objeto que sabe hablar con la base de datos (a traves del DAO).
    // El panel nunca toca la base de datos directo, solo llama a esto.
    private final InventarioServicio inventarioServicio;

    // Mientras esto sea true, se usan los productos de cargarDatosPrueba()
    // en vez de ir a la base de datos real. Se cambia a false cuando el
    // backend real ya esta listo para esta pantalla.
    private final boolean modoPrueba;

    // Componentes visuales que se usan en varios metodos, por eso son
    // atributos de la clase en vez de variables locales.
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JButton btnMover;
    private JButton btnNuevoProducto;
    private JButton btnEditarProducto;
    private JButton btnRefrescar;

    // Lista en memoria para desarrollo independiente (modo prueba)
    private List<ProductoDemo> productosDemo;

    // --- Filtro de columnas de stock segun el rol del usuario logueado ---
    // Se calculan UNA sola vez, en el constructor, antes de armar la tabla.
    private boolean mostrarStockTienda;
    private boolean mostrarStockTaller;

    // Guardan en que posicion (columna) de la tabla quedo cada dato.
    // -1 significa "esa columna no existe para este usuario" — se usa
    // despues para no intentar leer una columna que nunca se creo.
    private int colStockMin;
    private int colStockTienda = -1;
    private int colStockTaller = -1;

    public InventarioPanel() {
        this.inventarioServicio = new InventarioServicio();
        this.modoPrueba = true; // TODO: Cambiar a false al conectar con el backend real

        // Hay que saber el rol ANTES de construir la tabla, porque decide
        // cuantas columnas va a tener.
        determinarVisibilidadPorRol();

        if (modoPrueba) {
            cargarDatosPrueba();
        }
        initComponents();
        cargarDatosTabla();
    }

    // Traduce el rol del usuario logueado (Sesion es el singleton que
    // guarda quien inicio sesion en toda la app) a que columnas de stock
    // puede ver: Administrador ve todo, Cajero solo Tienda, Recepcion de
    // Taller y Tecnico solo Taller.
    private void determinarVisibilidadPorRol() {
        Usuario activo = Sesion.obtenerInstancia().getUsuarioActivo();
        Rol rol = (activo != null) ? activo.getRol() : null;
        mostrarStockTienda = (rol == Rol.ADMINISTRADOR || rol == Rol.CAJERO);
        mostrarStockTaller = (rol == Rol.ADMINISTRADOR || rol == Rol.RECEPCION_TALLER || rol == Rol.TECNICO);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Superior: Titulo y botones de accion ---
        JPanel panelTop = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestion de Inventario");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelTop.add(lblTitulo, BorderLayout.WEST);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnMover = new JButton("Mover Stock");
        btnNuevoProducto = new JButton("Nuevo Producto");
        btnEditarProducto = new JButton("Editar Producto");
        btnRefrescar = new JButton("Refrescar");
        panelAcciones.add(btnMover);
        panelAcciones.add(btnNuevoProducto);
        panelAcciones.add(btnEditarProducto);
        panelAcciones.add(btnRefrescar);
        panelTop.add(panelAcciones, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // --- Panel Central: Tabla de Productos ---
        // Las columnas base (ID..Stock Min.) las ve todo el mundo.
        // "Stock Tienda" y "Stock Taller" se agregan SOLO si el rol tiene
        // permiso de verlas — por eso la lista de columnas se arma con un
        // ArrayList dinamico, no con un arreglo fijo.
        List<String> columnasVisibles = new ArrayList<>();
        columnasVisibles.add("ID");
        columnasVisibles.add("Nombre");
        columnasVisibles.add("Descripcion");
        columnasVisibles.add("Precio Venta");
        columnasVisibles.add("Costo");
        columnasVisibles.add("Stock Min.");
        colStockMin = 5;

        int indiceSiguiente = 6;
        if (mostrarStockTienda) {
            columnasVisibles.add("Stock Tienda");
            colStockTienda = indiceSiguiente++;
        }
        if (mostrarStockTaller) {
            columnasVisibles.add("Stock Taller");
            colStockTaller = indiceSiguiente++;
        }
        String[] columnas = columnasVisibles.toArray(new String[0]);

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Nadie edita la tabla escribiendo directo en las celdas
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setRowHeight(24);

        // Pinta de rojo claro las filas bajo el stock minimo (clase de abajo)
        tablaProductos.setDefaultRenderer(Object.class, new RenderizadorStockMinimo());

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);

        // --- Conectar cada boton con su accion ---
        btnMover.addActionListener(e -> abrirDialogoMover());
        btnNuevoProducto.addActionListener(e -> abrirDialogoProducto(null));
        btnEditarProducto.addActionListener(e -> editarProductoSeleccionado());
        btnRefrescar.addActionListener(e -> cargarDatosTabla());

        // Solo Administrador y Recepcion de Taller pueden traspasar stock
        // (segun Sesion.tienePermiso("MOVER_STOCK")). Un Cajero o Tecnico
        // ve el boton pero deshabilitado.
        btnMover.setEnabled(Sesion.obtenerInstancia().tienePermiso("MOVER_STOCK"));
    }

    /**
     * Vuelve a llenar la tabla desde cero: borra todas las filas y las
     * agrega de nuevo, ya sea desde la lista de prueba o desde el backend.
     */
    private void cargarDatosTabla() {
        modeloTabla.setRowCount(0);
        if (modoPrueba) {
            for (ProductoDemo pd : productosDemo) {
                modeloTabla.addRow(construirFila(pd.producto, pd.stockTienda, pd.stockTaller));
            }
        } else {
            try {
                List<Producto> lista = inventarioServicio.listarProductos();
                for (Producto p : lista) {
                    // Si el rol no ve esa ubicacion, ni se consulta (ahorra una llamada).
                    int stockTienda = mostrarStockTienda ? inventarioServicio.consultarStock(p.getId(), Ubicacion.TIENDA) : 0;
                    int stockTaller = mostrarStockTaller ? inventarioServicio.consultarStock(p.getId(), Ubicacion.TALLER) : 0;
                    modeloTabla.addRow(construirFila(p, stockTienda, stockTaller));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Arma una fila de la tabla incluyendo solo las columnas de stock que
    // le corresponden al rol del usuario logueado. La reutilizan tanto
    // los datos de prueba como los reales, para no repetir el mismo codigo.
    private Object[] construirFila(Producto p, int stockTienda, int stockTaller) {
        List<Object> fila = new ArrayList<>();
        fila.add(p.getId());
        fila.add(p.getNombre());
        fila.add(p.getDescripcion());
        fila.add(String.format("$%.2f", p.getPrecioVenta()));
        fila.add(String.format("$%.2f", p.getCosto()));
        fila.add(p.getStockMinimo());
        if (mostrarStockTienda) {
            fila.add(stockTienda);
        }
        if (mostrarStockTaller) {
            fila.add(stockTaller);
        }
        return fila.toArray();
    }

    /**
     * Ventana emergente (JDialog) para traspasar cantidad de un producto
     * entre Tienda y Taller.
     */
    private void abrirDialogoMover() {
        int filaSel = tablaProductos.getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para mover stock.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idProd = (int) modeloTabla.getValueAt(filaSel, 0);
        String nombreProd = (String) modeloTabla.getValueAt(filaSel, 1);
        // Si el usuario no ve alguna de las dos columnas de stock, se asume 0
        // para esa ubicacion (no deberia poder mover algo que ni siquiera ve).
        int stockTiendaActual = (colStockTienda != -1) ? (int) modeloTabla.getValueAt(filaSel, colStockTienda) : 0;
        int stockTallerActual = (colStockTaller != -1) ? (int) modeloTabla.getValueAt(filaSel, colStockTaller) : 0;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Traspaso de Stock", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: nombre del producto (solo texto, informativo)
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JLabel(nombreProd), gbc);

        // Fila 1: combo de Origen
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Origen:"), gbc);
        gbc.gridx = 1;
        JComboBox<Ubicacion> cbOrigen = new JComboBox<>(Ubicacion.values());
        dialog.add(cbOrigen, gbc);

        // Fila 2: combo de Destino
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Destino:"), gbc);
        gbc.gridx = 1;
        JComboBox<Ubicacion> cbDestino = new JComboBox<>(Ubicacion.values());
        cbDestino.setSelectedItem(Ubicacion.TALLER);
        dialog.add(cbDestino, gbc);

        // --- Bloqueo de Origen/Destino segun el rol ---
        // Si el usuario solo administra UNA ubicacion (por ejemplo Recepcion
        // de Taller, que solo ve Taller), no tiene sentido dejarlo elegir
        // un origen que ni siquiera puede ver. En ese caso:
        //   - El Origen se fija a esa unica ubicacion y se bloquea.
        //   - El Destino se fija a la otra ubicacion (la unica posible)
        //     y tambien se bloquea, porque solo hay dos ubicaciones en total.
        // El Administrador ve ambas ubicaciones, asi que para el los dos
        // combos quedan libres, igual que antes.
        Ubicacion ubicacionFija = null;
        if (mostrarStockTienda && !mostrarStockTaller) {
            ubicacionFija = Ubicacion.TIENDA;
        } else if (mostrarStockTaller && !mostrarStockTienda) {
            ubicacionFija = Ubicacion.TALLER;
        }
        if (ubicacionFija != null) {
            cbOrigen.setSelectedItem(ubicacionFija);
            cbOrigen.setEnabled(false); // el usuario ya no puede tocar este combo

            Ubicacion destinoFijo = (ubicacionFija == Ubicacion.TIENDA) ? Ubicacion.TALLER : Ubicacion.TIENDA;
            cbDestino.setSelectedItem(destinoFijo);
            // Destino queda habilitado (a diferencia de Origen)
        }
        
        
        // Fila 3: cantidad a mover
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Cantidad a mover:"), gbc);
        gbc.gridx = 1;
        JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        dialog.add(spCantidad, gbc);

        // Fila 4: boton de confirmar
        JButton btnConfirmar = new JButton("Confirmar Traspaso");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(btnConfirmar, gbc);

        // Si el Administrador cambia el Origen manualmente, el Destino se
        // ajusta solo a la otra ubicacion (esto NO se ejecuta si el combo
        // esta bloqueado, porque entonces el usuario no puede cambiarlo).
        cbOrigen.addActionListener(e -> {
            if (cbOrigen.getSelectedItem() == Ubicacion.TIENDA) {
                cbDestino.setSelectedItem(Ubicacion.TALLER);
            } else {
                cbDestino.setSelectedItem(Ubicacion.TIENDA);
            }
        });

        // Logica que corre al presionar "Confirmar Traspaso"
        btnConfirmar.addActionListener(e -> {
            Ubicacion origen = (Ubicacion) cbOrigen.getSelectedItem();
            Ubicacion destino = (Ubicacion) cbDestino.getSelectedItem();
            int cantidad = (int) spCantidad.getValue();

            if (origen == destino) {
                JOptionPane.showMessageDialog(dialog, "El origen y destino deben ser diferentes.",
                        "Error de Validacion", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Valida que haya suficiente stock en el origen antes de mover nada
            int stockDisponible = (origen == Ubicacion.TIENDA) ? stockTiendaActual : stockTallerActual;
            if (cantidad > stockDisponible) {
                JOptionPane.showMessageDialog(dialog,
                        "Stock insuficiente en origen (" + origen + "). Disponible: " + stockDisponible,
                        "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Procesa el traspaso: en modo prueba solo actualiza los numeros
            // en memoria; con el backend real, delega todo a InventarioServicio.
            try {
                if (modoPrueba) {
                    ProductoDemo pd = buscarDemoPorId(idProd);
                    if (origen == Ubicacion.TIENDA) {
                        pd.stockTienda -= cantidad;
                        pd.stockTaller += cantidad;
                    } else {
                        pd.stockTaller -= cantidad;
                        pd.stockTienda += cantidad;
                    }
                } else {
                    inventarioServicio.transferir(idProd, origen, destino, cantidad);
                }
                JOptionPane.showMessageDialog(dialog, "Traspaso realizado con exito.");
                dialog.dispose();
                cargarDatosTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al transferir: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Ventana emergente para Crear o Editar un producto.
     * Si productoExistente es null, es "crear"; si no, es "editar" ese producto.
     */
    private void abrirDialogoProducto(Producto productoExistente) {
        boolean esEdicion = (productoExistente != null);
        String titulo = esEdicion ? "Editar Producto" : "Nuevo Producto";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(380, 320);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNombre = new JTextField(15);
        JTextField txtDescripcion = new JTextField(15);
        JTextField txtCategoriaId = new JTextField("1", 15);
        JTextField txtPrecioVenta = new JTextField(15);
        JTextField txtCosto = new JTextField(15);
        JTextField txtStockMinimo = new JTextField(15);

        // Si es edicion, precarga los campos con los datos actuales del producto
        if (esEdicion) {
            txtNombre.setText(productoExistente.getNombre());
            txtDescripcion.setText(productoExistente.getDescripcion());
            txtCategoriaId.setText(String.valueOf(productoExistente.getIdCategoria()));
            txtPrecioVenta.setText(String.valueOf(productoExistente.getPrecioVenta()));
            txtCosto.setText(String.valueOf(productoExistente.getCosto()));
            txtStockMinimo.setText(String.valueOf(productoExistente.getStockMinimo()));
        }

        int fila = 0;
        agregarCampo(dialog, gbc, "Nombre:", txtNombre, fila++);
        agregarCampo(dialog, gbc, "Descripcion:", txtDescripcion, fila++);
        agregarCampo(dialog, gbc, "ID Categoria:", txtCategoriaId, fila++);
        agregarCampo(dialog, gbc, "Precio Venta:", txtPrecioVenta, fila++);
        agregarCampo(dialog, gbc, "Costo:", txtCosto, fila++);
        agregarCampo(dialog, gbc, "Stock Minimo:", txtStockMinimo, fila++);

        JButton btnGuardar = new JButton(esEdicion ? "Actualizar" : "Guardar");
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        dialog.add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String desc = txtDescripcion.getText().trim();
                int idCat = Integer.parseInt(txtCategoriaId.getText().trim());
                double precio = Double.parseDouble(txtPrecioVenta.getText().trim());
                double costo = Double.parseDouble(txtCosto.getText().trim());
                int stockMin = Integer.parseInt(txtStockMinimo.getText().trim());

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio.", "Validacion", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (esEdicion) {
                    // Modifica el mismo objeto que ya estaba en la tabla/lista
                    productoExistente.setNombre(nombre);
                    productoExistente.setDescripcion(desc);
                    productoExistente.setIdCategoria(idCat);
                    productoExistente.setPrecioVenta(precio);
                    productoExistente.setCosto(costo);
                    productoExistente.setStockMinimo(stockMin);
                    if (!modoPrueba) {
                        inventarioServicio.actualizarProducto(productoExistente);
                    }
                } else {
                    // Crea un producto nuevo desde cero
                    Producto nuevo = new Producto();
                    nuevo.setNombre(nombre);
                    nuevo.setDescripcion(desc);
                    nuevo.setIdCategoria(idCat);
                    nuevo.setPrecioVenta(precio);
                    nuevo.setCosto(costo);
                    nuevo.setStockMinimo(stockMin);
                    if (modoPrueba) {
                        nuevo.setId(productosDemo.size() + 1);
                        productosDemo.add(new ProductoDemo(nuevo, 0, 0));
                    } else {
                        inventarioServicio.crearProducto(nuevo);
                    }
                }

                JOptionPane.showMessageDialog(dialog, "Producto guardado correctamente.");
                dialog.dispose();
                cargarDatosTabla();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Revise los campos numericos (Precio, Costo, Stock Minimo, Categoria).",
                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // Metodo de apoyo: agrega una etiqueta + un campo de texto en una fila
    // del formulario, para no repetir las mismas 4 lineas 6 veces.
    private void agregarCampo(JDialog d, GridBagConstraints gbc, String label, JTextField tf, int fila) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1;
        d.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        d.add(tf, gbc);
    }

    // Toma la fila seleccionada en la tabla y abre el dialogo de edicion
    // con ese producto ya cargado.
    private void editarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        if (modoPrueba) {
            ProductoDemo pd = buscarDemoPorId(id);
            if (pd != null) abrirDialogoProducto(pd.producto);
        } else {
            try {
                List<Producto> lista = inventarioServicio.listarProductos();
                Producto actual = lista.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
                if (actual != null) {
                    abrirDialogoProducto(actual);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al recuperar datos del producto: " + ex.getMessage());
            }
        }
    }

    // --- RENDERIZADOR PERSONALIZADO PARA ALERTAR STOCK MINIMO ---
    // Un renderer en Swing decide COMO se pinta cada celda de la tabla.
    // Esta clase hereda el comportamiento normal (DefaultTableCellRenderer)
    // y solo le agrega: si el stock total (de lo que ese rol puede ver) es
    // menor o igual al stock minimo, pinta la fila de rojo claro.
    private class RenderizadorStockMinimo extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            int stockMin = (int) table.getValueAt(row, colStockMin);
            int stockTotal = 0;
            if (colStockTienda != -1) {
                stockTotal += (int) table.getValueAt(row, colStockTienda);
            }
            if (colStockTaller != -1) {
                stockTotal += (int) table.getValueAt(row, colStockTaller);
            }

            if (!isSelected) {
                if (stockTotal <= stockMin) {
                    c.setBackground(new Color(255, 200, 200)); // Rojo claro = alerta
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
            return c;
        }
    }

    // --- DATOS DE PRUEBA (solo se usan mientras modoPrueba = true) ---
    private void cargarDatosPrueba() {
        productosDemo = new ArrayList<>();

        Producto p1 = new Producto();
        p1.setId(1); p1.setNombre("Aceite Sintetico 5W30"); p1.setDescripcion("4 Litros");
        p1.setPrecioVenta(45.00); p1.setCosto(25.00); p1.setStockMinimo(10);
        productosDemo.add(new ProductoDemo(p1, 5, 3)); // Total 8 < 10 (Alerta)

        Producto p2 = new Producto();
        p2.setId(2); p2.setNombre("Lija de Agua 2000"); p2.setDescripcion("Uso pintura");
        p2.setPrecioVenta(1.50); p2.setCosto(0.50); p2.setStockMinimo(20);
        productosDemo.add(new ProductoDemo(p2, 50, 15)); // Total 65 (Ok)

        Producto p3 = new Producto();
        p3.setId(3); p3.setNombre("Pistola de Pintar"); p3.setDescripcion("HVLP Profesional");
        p3.setPrecioVenta(120.00); p3.setCosto(70.00); p3.setStockMinimo(5);
        productosDemo.add(new ProductoDemo(p3, 2, 1)); // Total 3 < 5 (Alerta)
    }

    // Busca un producto de prueba por su ID dentro de la lista en memoria
    private ProductoDemo buscarDemoPorId(int id) {
        return productosDemo.stream().filter(p -> p.producto.getId() == id).findFirst().orElse(null);
    }

    // Clase auxiliar SOLO para modo prueba: junta un Producto con sus
    // cantidades de stock en Tienda y Taller (en la base de datos real,
    // eso lo maneja la tabla stock_ubicacion, no esta clase).
    private static class ProductoDemo {
        Producto producto;
        int stockTienda;
        int stockTaller;

        ProductoDemo(Producto producto, int stockTienda, int stockTaller) {
            this.producto = producto;
            this.stockTienda = stockTienda;
            this.stockTaller = stockTaller;
        }
    }
}
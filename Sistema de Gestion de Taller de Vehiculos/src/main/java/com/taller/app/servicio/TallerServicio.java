package com.taller.app.servicio;

import java.time.LocalDateTime;
import java.util.List;

import com.taller.app.dao.ClienteDAO;
import com.taller.app.dao.DetalleOrdenProductoDAO;
import com.taller.app.dao.DetalleOrdenServicioDAO;
import com.taller.app.dao.OrdenServicioDAO;
import com.taller.app.dao.ServicioDAO;
import com.taller.app.dao.VehiculoDAO;
import com.taller.app.dao.impl.ClienteDAOImpl;
import com.taller.app.dao.impl.DetalleOrdenProductoDAOImpl;
import com.taller.app.dao.impl.DetalleOrdenServicioDAOImpl;
import com.taller.app.dao.impl.OrdenServicioDAOImpl;
import com.taller.app.dao.impl.ServicioDAOImpl;
import com.taller.app.dao.impl.VehiculoDAOImpl;
import com.taller.app.modelo.Cliente;
import com.taller.app.modelo.DetalleOrdenProducto;
import com.taller.app.modelo.DetalleOrdenServicio;
import com.taller.app.modelo.EstadoOrden;
import com.taller.app.modelo.OrdenServicio;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.Servicio;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.modelo.Usuario;
import com.taller.app.modelo.Vehiculo;
import com.taller.app.util.Validaciones;

public class TallerServicio {

    // Cada DAO habla con una tabla distinta; esta clase los combina para
    // implementar las reglas de negocio del módulo de Taller.
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAOImpl();
    private final OrdenServicioDAO ordenDAO = new OrdenServicioDAOImpl();
    private final DetalleOrdenServicioDAO detalleServicioDAO = new DetalleOrdenServicioDAOImpl();
    private final DetalleOrdenProductoDAO detalleProductoDAO = new DetalleOrdenProductoDAOImpl();
    
    // Se reutiliza InventarioServicio en vez de hablar directo con StockDAO,
    // así el control de stock queda en un solo lugar del sistema.
    private final InventarioServicio inventarioServicio = new InventarioServicio();
    
    
    // ServicioDAO habla con la tabla de "servicios" (el catalogo del taller,
    // ej. "pintura completa") — no confundir con este paquete servicio/.
    // Se necesita para poder listar el catalogo completo en la pantalla.
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();

    // --- Clientes y vehículos ---

    public void registrarCliente(Cliente cliente) {
        // Valida antes de tocar la base de datos: nunca confiar en que la
        // pantalla ya validó todo.
        if (Validaciones.esVacio(cliente.getNombre())) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        clienteDAO.crear(cliente);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        if (Validaciones.esVacio(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("La placa del vehículo no puede estar vacía.");
        }
        vehiculoDAO.crear(vehiculo);
    }

    // Historial de un vehículo: todas las órdenes que ha tenido a lo largo del tiempo.
    public List<OrdenServicio> historialVehiculo(int idVehiculo) {
        return ordenDAO.listarPorVehiculo(idVehiculo);
    }
    public List<Cliente> listarClientes() {
        return clienteDAO.listar();
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculoDAO.listar();
    }

    public List<Servicio> listarCatalogoServicios() {
        return servicioDAO.listar();
    }

    public List<Producto> listarProductosDisponibles() {
        return inventarioServicio.listarProductos();
    }

    public List<OrdenServicio> listarOrdenes() {
        return ordenDAO.listar();
    }
    
    
    
    // --- Órdenes de servicio ---

    // Crea la orden con estado inicial RECIBIDO y sin servicios/productos todavía.
    public OrdenServicio crearOrden(Vehiculo vehiculo, Cliente cliente, Usuario usuarioRecibe) {
        OrdenServicio orden = new OrdenServicio();
        orden.setIdVehiculo(vehiculo.getId());
        orden.setIdCliente(cliente.getId());
        orden.setIdUsuarioRecibe(usuarioRecibe.getId());
        orden.setFechaIngreso(LocalDateTime.now());
        orden.setEstado(EstadoOrden.RECIBIDO);
        orden.setTotal(0);
        ordenDAO.crear(orden);

        // MySQL le asigna el id automáticamente al insertar; como el INSERT no
        // lo devuelve, la volvemos a buscar para saber cuál id le tocó.
        List<OrdenServicio> ordenes = ordenDAO.listarPorVehiculo(vehiculo.getId());
        return ordenes.get(ordenes.size() - 1);
    }

    // Agrega una línea de servicio (ej. "pintura completa") a una orden existente.
    public void agregarServicio(OrdenServicio orden, Servicio servicio, int cantidad) {
        if (!Validaciones.esCantidadValida(cantidad)) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        DetalleOrdenServicio detalle = new DetalleOrdenServicio();
        detalle.setIdOrden(orden.getId());
        detalle.setIdServicio(servicio.getId());
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(servicio.getPrecioBase());
        detalle.setSubtotal(servicio.getPrecioBase() * cantidad);
        detalleServicioDAO.crear(detalle);

        // Cada vez que se agrega algo, se recalcula el total de la orden completa.
        actualizarTotal(orden);
    }

    // Agrega un producto/insumo usado en la orden (ej. lija, pintura) y descuenta
    // esa cantidad del stock de TALLER automáticamente.
    public void agregarProducto(OrdenServicio orden, Producto producto, int cantidad) {
        if (!Validaciones.esCantidadValida(cantidad)) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        // Antes de usar el producto, confirma que hay suficiente en el taller.
        int disponible = inventarioServicio.consultarStock(producto.getId(), Ubicacion.TALLER);
        if (disponible < cantidad) {
            throw new IllegalStateException("No hay suficiente stock en taller de " + producto.getNombre());
        }

        DetalleOrdenProducto detalle = new DetalleOrdenProducto();
        detalle.setIdOrden(orden.getId());
        detalle.setIdProducto(producto.getId());
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecioVenta());
        detalle.setSubtotal(producto.getPrecioVenta() * cantidad);
        detalleProductoDAO.crear(detalle);

        // Como el producto ya se "usó" en la orden, se resta del inventario del taller.
        inventarioServicio.descontarStock(producto.getId(), Ubicacion.TALLER, cantidad);

        actualizarTotal(orden);
    }

    // Cambia el estado de la orden, pero solo si es "un paso hacia adelante"
    // (RECIBIDO -> EN_PROCESO -> TERMINADO -> ENTREGADO). No deja saltarse pasos
    // ni retroceder, porque el enum EstadoOrden está declarado en ese orden y
    // usamos su posición (ordinal) para comparar.
    public void cambiarEstado(OrdenServicio orden, EstadoOrden nuevoEstado) {
        EstadoOrden actual = orden.getEstado();
        if (nuevoEstado.ordinal() != actual.ordinal() + 1) {
            throw new IllegalStateException("No se puede pasar de " + actual + " a " + nuevoEstado + " directamente.");
        }
        orden.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoOrden.ENTREGADO) {
            // Solo se registra la fecha real de entrega cuando de verdad se entrega.
            orden.setFechaEntregaReal(LocalDateTime.now());
        }
        ordenDAO.actualizar(orden);
    }

    // Asigna qué técnico va a trabajar esta orden (se usa luego para que un
    // Técnico solo vea/actualice sus propias órdenes desde la pantalla).
    public void asignarTecnico(OrdenServicio orden, Usuario tecnico) {
        orden.setIdUsuarioTecnico(tecnico.getId());
        ordenDAO.actualizar(orden);
    }

    // Suma todas las líneas de servicios + todas las líneas de productos de
    // la orden. Se puede llamar sola (solo para consultar) o desde
    // actualizarTotal() (para guardar el resultado en la base).
    public double calcularTotal(OrdenServicio orden) {
        double total = 0;
        for (DetalleOrdenServicio d : detalleServicioDAO.listarPorOrden(orden.getId())) {
            total += d.getSubtotal();
        }
        for (DetalleOrdenProducto d : detalleProductoDAO.listarPorOrden(orden.getId())) {
            total += d.getSubtotal();
        }
        orden.setTotal(total);
        return total;
    }
    
    public List<DetalleOrdenServicio> listarServiciosDeOrden(OrdenServicio orden) {
        return detalleServicioDAO.listarPorOrden(orden.getId());
    }

    public List<DetalleOrdenProducto> listarProductosDeOrden(OrdenServicio orden) {
        return detalleProductoDAO.listarPorOrden(orden.getId());
    }

    // Método privado de apoyo: recalcula y guarda el total en la base de datos.
    // Privado porque es un detalle interno, nadie fuera de esta clase lo necesita.
    private void actualizarTotal(OrdenServicio orden) {
        calcularTotal(orden);
        ordenDAO.actualizar(orden);
    }
}
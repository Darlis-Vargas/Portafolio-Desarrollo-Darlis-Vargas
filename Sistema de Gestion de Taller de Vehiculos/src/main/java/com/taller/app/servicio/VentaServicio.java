package com.taller.app.servicio;

import java.util.List;

import com.taller.app.dao.DetalleVentaDAO;
import com.taller.app.dao.ProductoDAO;
import com.taller.app.dao.VentaDAO;
import com.taller.app.dao.impl.DetalleVentaDAOImpl;
import com.taller.app.dao.impl.ProductoDAOImpl;
import com.taller.app.dao.impl.VentaDAOImpl;
import com.taller.app.modelo.DetalleVenta;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.modelo.Usuario;
import com.taller.app.modelo.Venta;
import com.taller.app.util.Validaciones;

public class VentaServicio {

    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();
    private final InventarioServicio inventarioServicio = new InventarioServicio();

    public List<Producto> listarProductosDisponibles() {
        return productoDAO.listar();
    }

    public void agregarProducto(Venta venta, Producto producto, int cantidad) {
        if (!Validaciones.esCantidadValida(cantidad)) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        int disponible = inventarioServicio.consultarStock(producto.getId(), Ubicacion.TIENDA);
        if (disponible < cantidad) {
            throw new IllegalStateException("No hay suficiente stock en tienda de " + producto.getNombre());
        }

        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdProducto(producto.getId());
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecioVenta());
        detalle.setSubtotal(producto.getPrecioVenta() * cantidad);

        venta.getItems().add(detalle);
    }

    public double calcularTotal(Venta venta) {
        double total = 0;
        for (DetalleVenta d : venta.getItems()) {
            total += d.getSubtotal();
        }
        venta.setTotal(total);
        return total;
    }

    public void registrar(Venta venta) {
        if (venta.getItems().isEmpty()) {
            throw new IllegalStateException("La venta no tiene productos.");
        }

        Usuario activo = Sesion.obtenerInstancia().getUsuarioActivo();
        venta.setIdUsuarioCajero(activo != null ? activo.getId() : venta.getIdUsuarioCajero());
        venta.setFecha(java.time.LocalDateTime.now());
        calcularTotal(venta);

        ventaDAO.crear(venta);

        List<Venta> ventasDelDia = ventaDAO.listarPorFecha(java.time.LocalDate.now(), java.time.LocalDate.now());
        Venta ventaGuardada = ventasDelDia.get(ventasDelDia.size() - 1);

        for (DetalleVenta d : venta.getItems()) {
            d.setIdVenta(ventaGuardada.getId());
            detalleVentaDAO.crear(d);
            inventarioServicio.descontarStock(d.getIdProducto(), Ubicacion.TIENDA, d.getCantidad());
        }

        venta.getItems().clear();
    }
}
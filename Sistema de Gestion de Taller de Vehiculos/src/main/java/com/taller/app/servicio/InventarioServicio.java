package com.taller.app.servicio;

import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.MovimientoStockDAO;
import com.taller.app.dao.ProductoDAO;
import com.taller.app.dao.StockDAO;
import com.taller.app.dao.impl.MovimientoStockDAOImpl;
import com.taller.app.dao.impl.ProductoDAOImpl;
import com.taller.app.dao.impl.StockDAOImpl;
import com.taller.app.modelo.MovimientoStock;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.StockUbicacion;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.modelo.Usuario;
import com.taller.app.util.Validaciones;

public class InventarioServicio {

    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final StockDAO stockDAO = new StockDAOImpl();
    private final MovimientoStockDAO movimientoDAO = new MovimientoStockDAOImpl();

    public List<Producto> listarProductos() {
        return productoDAO.listar();
    }

    public int consultarStock(int idProducto, Ubicacion ubicacion) {
        StockUbicacion stock = stockDAO.buscarPorProductoYUbicacion(idProducto, ubicacion);
        return stock != null ? stock.getCantidad() : 0;
    }

    public void transferir(int idProducto, Ubicacion origen, Ubicacion destino, int cantidad) {
        if (!Validaciones.esCantidadValida(cantidad)) {
            throw new IllegalArgumentException("La cantidad a transferir debe ser mayor a cero.");
        }

        StockUbicacion stockOrigen = stockDAO.buscarPorProductoYUbicacion(idProducto, origen);
        if (stockOrigen == null || stockOrigen.getCantidad() < cantidad) {
            throw new IllegalStateException("No hay suficiente stock en " + origen + " para transferir.");
        }
        stockDAO.actualizarCantidad(stockOrigen.getId(), stockOrigen.getCantidad() - cantidad);

        StockUbicacion stockDestino = stockDAO.buscarPorProductoYUbicacion(idProducto, destino);
        if (stockDestino == null) {
            StockUbicacion nuevo = new StockUbicacion();
            nuevo.setIdProducto(idProducto);
            nuevo.setUbicacion(destino);
            nuevo.setCantidad(cantidad);
            stockDAO.crear(nuevo);
        } else {
            stockDAO.actualizarCantidad(stockDestino.getId(), stockDestino.getCantidad() + cantidad);
        }

        registrarMovimiento(idProducto, origen, destino, cantidad, "traspaso");
    }

    public void descontarStock(int idProducto, Ubicacion ubicacion, int cantidad) {
        StockUbicacion stock = stockDAO.buscarPorProductoYUbicacion(idProducto, ubicacion);
        if (stock == null || stock.getCantidad() < cantidad) {
            throw new IllegalStateException("No hay suficiente stock en " + ubicacion + " para descontar.");
        }
        stockDAO.actualizarCantidad(stock.getId(), stock.getCantidad() - cantidad);
        registrarMovimiento(idProducto, ubicacion, null, cantidad, "salida");
    }

    public List<Producto> alertaStockMinimo(Ubicacion ubicacion) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productoDAO.listar()) {
            int cantidad = consultarStock(p.getId(), ubicacion);
            if (cantidad < p.getStockMinimo()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public void crearProducto(Producto producto) {
        if (Validaciones.esVacio(producto.getNombre())) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (!Validaciones.esPrecioValido(producto.getPrecioVenta())) {
            throw new IllegalArgumentException("El precio de venta no es válido.");
        }
        productoDAO.crear(producto);
    }

    public void actualizarProducto(Producto producto) {
        productoDAO.actualizar(producto);
    }

    private void registrarMovimiento(int idProducto, Ubicacion origen, Ubicacion destino, int cantidad, String tipo) {
        MovimientoStock mov = new MovimientoStock();
        mov.setIdProducto(idProducto);
        mov.setUbicacionOrigen(origen);
        mov.setUbicacionDestino(destino);
        mov.setCantidad(cantidad);
        Usuario activo = Sesion.obtenerInstancia().getUsuarioActivo();
        mov.setIdUsuario(activo != null ? activo.getId() : 1);
        mov.setFecha(java.time.LocalDateTime.now());
        mov.setTipo(tipo);
        movimientoDAO.crear(mov);
    }
}
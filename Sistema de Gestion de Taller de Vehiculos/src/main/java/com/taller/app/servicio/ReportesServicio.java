package com.taller.app.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.OrdenServicioDAO;
import com.taller.app.dao.ProductoDAO;
import com.taller.app.dao.VentaDAO;
import com.taller.app.dao.impl.OrdenServicioDAOImpl;
import com.taller.app.dao.impl.ProductoDAOImpl;
import com.taller.app.dao.impl.VentaDAOImpl;
import com.taller.app.modelo.EstadoOrden;
import com.taller.app.modelo.OrdenServicio;
import com.taller.app.modelo.Producto;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.modelo.Venta;

public class ReportesServicio {

    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final OrdenServicioDAO ordenDAO = new OrdenServicioDAOImpl();
    private final InventarioServicio inventarioServicio = new InventarioServicio();

    // Ventas registradas entre dos fechas (inclusive).
    public List<Venta> reporteVentas(LocalDate desde, LocalDate hasta) {
        return ventaDAO.listarPorFecha(desde, hasta);
    }

    // Productos bajo su stock mínimo, revisando tienda y taller juntos
    // (si está bajo en cualquiera de las dos ubicaciones, aparece en el reporte).
    public List<Producto> reporteStockBajo() {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productoDAO.listar()) {
            int enTienda = inventarioServicio.consultarStock(p.getId(), Ubicacion.TIENDA);
            int enTaller = inventarioServicio.consultarStock(p.getId(), Ubicacion.TALLER);
            if (enTienda < p.getStockMinimo() || enTaller < p.getStockMinimo()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Órdenes de servicio agrupadas por su estado actual.
    public List<OrdenServicio> reporteOrdenesPorEstado(EstadoOrden estado) {
        return ordenDAO.listarPorEstado(estado);
    }
}
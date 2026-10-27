package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.DetalleVenta;

public interface DetalleVentaDAO {
    void crear(DetalleVenta detalle);
    List<DetalleVenta> listarPorVenta(int idVenta);
}

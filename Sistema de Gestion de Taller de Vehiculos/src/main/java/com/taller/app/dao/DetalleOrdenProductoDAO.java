package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.DetalleOrdenProducto;

public interface DetalleOrdenProductoDAO {
    void crear(DetalleOrdenProducto detalle);
    List<DetalleOrdenProducto> listarPorOrden(int idOrden);
}
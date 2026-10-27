package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.DetalleOrdenServicio;

public interface DetalleOrdenServicioDAO {
    void crear(DetalleOrdenServicio detalle);
    List<DetalleOrdenServicio> listarPorOrden(int idOrden);
}
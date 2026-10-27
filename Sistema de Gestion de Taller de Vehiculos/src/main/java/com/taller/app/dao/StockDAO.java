package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.StockUbicacion;
import com.taller.app.modelo.Ubicacion;

public interface StockDAO {
	StockUbicacion buscarPorProductoYUbicacion(int idProducto, Ubicacion ubicacion);
    List<StockUbicacion> listarPorUbicacion(Ubicacion ubicacion);
    void actualizarCantidad(int idStock, int nuevaCantidad);
    void crear(StockUbicacion stock);
}
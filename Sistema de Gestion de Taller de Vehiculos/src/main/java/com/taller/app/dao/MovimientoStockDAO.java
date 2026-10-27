package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.MovimientoStock;

public interface MovimientoStockDAO {
    void crear(MovimientoStock movimiento);
    List<MovimientoStock> listarPorProducto(int idProducto);
}
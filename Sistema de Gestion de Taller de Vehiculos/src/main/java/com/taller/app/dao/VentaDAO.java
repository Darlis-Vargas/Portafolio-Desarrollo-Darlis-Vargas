package com.taller.app.dao;

import java.time.LocalDate;
import java.util.List;
import com.taller.app.modelo.Venta;

public interface VentaDAO {
    Venta buscarPorId(int id);
    List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta);
    void crear(Venta venta);
}

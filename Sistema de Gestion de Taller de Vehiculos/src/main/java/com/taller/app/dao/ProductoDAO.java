package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.Producto;

public interface ProductoDAO {
    Producto buscarPorId(int id);
    List<Producto> listar();
    List<Producto> listarPorCategoria(int idCategoria);
    void crear(Producto producto);
    void actualizar(Producto producto);
}
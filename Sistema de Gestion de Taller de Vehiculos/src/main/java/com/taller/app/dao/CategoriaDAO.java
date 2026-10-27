package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.Categoria;

public interface CategoriaDAO {
    Categoria buscarPorId(int id);
    List<Categoria> listar();
    
    void crear(Categoria categoria);
    void actualizar(Categoria categoria);
}
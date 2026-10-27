package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.Servicio;

public interface ServicioDAO {
    Servicio buscarPorId(int id);
    List<Servicio> listar();
    void crear(Servicio servicio);
    void actualizar(Servicio servicio);
    
}

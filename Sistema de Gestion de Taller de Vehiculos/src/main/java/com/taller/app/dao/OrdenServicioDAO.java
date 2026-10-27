package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.EstadoOrden;
import com.taller.app.modelo.OrdenServicio;

public interface OrdenServicioDAO {
    OrdenServicio buscarPorId(int id);
    List<OrdenServicio> listarPorVehiculo(int idVehiculo);
    List<OrdenServicio> listarPorEstado(EstadoOrden estado);
    List<OrdenServicio> listarPorTecnico(int idUsuarioTecnico);
    void crear(OrdenServicio orden);
    void actualizar(OrdenServicio orden);
    List<OrdenServicio> listar();
    
}
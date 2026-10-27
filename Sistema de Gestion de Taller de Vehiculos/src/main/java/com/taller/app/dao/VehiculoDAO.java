package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.Vehiculo;

public interface VehiculoDAO {
    Vehiculo buscarPorId(int id);
    List<Vehiculo> listarPorCliente(int idCliente);
    void crear(Vehiculo vehiculo);
    void actualizar(Vehiculo vehiculo);
    List<Vehiculo> listar();
}

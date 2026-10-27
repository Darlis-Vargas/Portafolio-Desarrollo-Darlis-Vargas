package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.Cliente;

public interface ClienteDAO {
    Cliente buscarPorId(int id);
    List<Cliente> listar();
    void crear(Cliente cliente);
    void actualizar(Cliente cliente);
}
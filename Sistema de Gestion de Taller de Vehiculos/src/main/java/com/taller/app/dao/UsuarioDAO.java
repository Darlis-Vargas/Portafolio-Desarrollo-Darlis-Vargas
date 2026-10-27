package com.taller.app.dao;

import java.util.List;
import com.taller.app.modelo.Usuario;

public interface UsuarioDAO {
    Usuario buscarPorId(int id);
    Usuario buscarPorUsuario(String usuario);
    List<Usuario> listar();
    void crear(Usuario usuario);
    void actualizar(Usuario usuario);
    void cambiarEstado(int id, boolean activo);
    void actualizarPassword(int idUsuario, String passwordHash);
}
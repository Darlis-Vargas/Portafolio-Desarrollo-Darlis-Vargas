package com.taller.app.servicio;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.taller.app.dao.UsuarioDAO;
import com.taller.app.dao.impl.UsuarioDAOImpl;
import com.taller.app.modelo.Usuario;

public class AutenticacionServicio {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public boolean autenticar(String usuario, String password) {
        Usuario u = usuarioDAO.buscarPorUsuario(usuario);
        if (u == null || !u.isActivo()) {
            return false;
        }
        String hashIngresado = hashPassword(password);
        if (hashIngresado.equals(u.getPasswordHash())) {
            Sesion.obtenerInstancia().iniciarSesion(u);
            return true;
        }
        return false;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public void crearUsuario(Usuario usuario) {
        usuario.setPasswordHash(hashPassword(usuario.getPasswordHash()));
        usuarioDAO.crear(usuario);
    }

    
    public void actualizarUsuario(Usuario usuario) {
        usuarioDAO.actualizar(usuario);
    }
    
    
    public void cambiarPassword(int idUsuario, String nuevaPasswordPlano) {
        usuarioDAO.actualizarPassword(idUsuario, hashPassword(nuevaPasswordPlano));
    }

    
    public void cambiarEstadoUsuario(int idUsuario, boolean activo) {
        usuarioDAO.cambiarEstado(idUsuario, activo);
    }
    
    

    private String hashPassword(String passwordPlano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(passwordPlano.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }
}
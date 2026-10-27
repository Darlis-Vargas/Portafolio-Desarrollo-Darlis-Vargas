package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.CategoriaDAO;
import com.taller.app.modelo.Categoria;
import com.taller.app.util.ConexionBD;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar categoria por id", e);
        }
        return null;
    }

    @Override
    public List<Categoria> listar() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try (PreparedStatement ps = getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar categorias", e);
        }
        return categorias;
    }

    @Override
    public void crear(Categoria categoria) {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear categoria", e);
        }
    }

    @Override
    public void actualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id_categoria = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar categoria", e);
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id_categoria"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
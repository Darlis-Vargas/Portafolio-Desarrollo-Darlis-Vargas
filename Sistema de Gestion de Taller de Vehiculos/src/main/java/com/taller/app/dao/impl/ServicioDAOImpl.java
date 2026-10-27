package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.ServicioDAO;
import com.taller.app.modelo.Servicio;
import com.taller.app.util.ConexionBD;

public class ServicioDAOImpl implements ServicioDAO {

    @Override
    public Servicio buscarPorId(int id) {
        String sql = "SELECT * FROM servicios WHERE id_servicio = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar servicio por id", e);
        }
        return null;
    }

    @Override
    public List<Servicio> listar() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios";
        try (PreparedStatement ps = getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                servicios.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar servicios", e);
        }
        return servicios;
    }

    @Override
    public void crear(Servicio servicio) {
        String sql = "INSERT INTO servicios (nombre, descripcion, precio_base, duracion_estimada) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setString(2, servicio.getDescripcion());
            ps.setDouble(3, servicio.getPrecioBase());
            ps.setInt(4, servicio.getDuracionEstimada());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear servicio", e);
        }
    }

    @Override
    public void actualizar(Servicio servicio) {
        String sql = "UPDATE servicios SET nombre = ?, descripcion = ?, precio_base = ?, duracion_estimada = ? WHERE id_servicio = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setString(2, servicio.getDescripcion());
            ps.setDouble(3, servicio.getPrecioBase());
            ps.setInt(4, servicio.getDuracionEstimada());
            ps.setInt(5, servicio.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar servicio", e);
        }
    }

    private Servicio mapear(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id_servicio"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setPrecioBase(rs.getDouble("precio_base"));
        s.setDuracionEstimada(rs.getInt("duracion_estimada"));
        return s;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
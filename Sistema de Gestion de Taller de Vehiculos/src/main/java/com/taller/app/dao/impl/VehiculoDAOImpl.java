package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.VehiculoDAO;
import com.taller.app.modelo.Vehiculo;
import com.taller.app.util.ConexionBD;


public class VehiculoDAOImpl implements VehiculoDAO {

    @Override
    public Vehiculo buscarPorId(int id) {
        String sql = "SELECT * FROM vehiculos WHERE id_vehiculo = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar vehiculo por id", e);
        }
        return null;
    }

    @Override
    public List<Vehiculo> listarPorCliente(int idCliente) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos WHERE id_cliente = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar vehiculos por cliente", e);
        }
        return vehiculos;
    }


    @Override
    public List<Vehiculo> listar() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos";
        try (PreparedStatement ps = getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vehiculos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar vehiculos", e);
        }
        return vehiculos;
    }
   
    
    @Override
    public void crear(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (id_cliente, placa, marca, modelo, anio, color) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, vehiculo.getIdCliente());
            ps.setString(2, vehiculo.getPlaca());
            ps.setString(3, vehiculo.getMarca());
            ps.setString(4, vehiculo.getModelo());
            ps.setInt(5, vehiculo.getAnio());
            ps.setString(6, vehiculo.getColor());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear vehiculo", e);
        }
    }

    @Override
    public void actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculos SET placa = ?, marca = ?, modelo = ?, anio = ?, color = ? WHERE id_vehiculo = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getMarca());
            ps.setString(3, vehiculo.getModelo());
            ps.setInt(4, vehiculo.getAnio());
            ps.setString(5, vehiculo.getColor());
            ps.setInt(6, vehiculo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar vehiculo", e);
        }
    }

    private Vehiculo mapear(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id_vehiculo"));
        v.setIdCliente(rs.getInt("id_cliente"));
        v.setPlaca(rs.getString("placa"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnio(rs.getInt("anio"));
        v.setColor(rs.getString("color"));
        return v;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}

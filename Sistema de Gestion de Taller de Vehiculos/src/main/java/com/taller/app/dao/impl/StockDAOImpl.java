package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.StockDAO;
import com.taller.app.modelo.StockUbicacion;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.util.ConexionBD;

public class StockDAOImpl implements StockDAO {

    @Override
    public StockUbicacion buscarPorProductoYUbicacion(int idProducto, Ubicacion ubicacion) {
        String sql = "SELECT * FROM stock_ubicacion WHERE id_producto = ? AND ubicacion = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.setString(2, ubicacion.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar stock por producto y ubicacion", e);
        }
        return null;
    }

    @Override
    public List<StockUbicacion> listarPorUbicacion(Ubicacion ubicacion) {
        List<StockUbicacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM stock_ubicacion WHERE ubicacion = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, ubicacion.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar stock por ubicacion", e);
        }
        return lista;
    }

    @Override
    public void actualizarCantidad(int idStock, int nuevaCantidad) {
        String sql = "UPDATE stock_ubicacion SET cantidad = ? WHERE id_stock = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idStock);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cantidad de stock", e);
        }
    }

    @Override
    public void crear(StockUbicacion stock) {
        String sql = "INSERT INTO stock_ubicacion (id_producto, ubicacion, cantidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, stock.getIdProducto());
            ps.setString(2, stock.getUbicacion().name());
            ps.setInt(3, stock.getCantidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear registro de stock", e);
        }
    }

    private StockUbicacion mapear(ResultSet rs) throws SQLException {
        StockUbicacion s = new StockUbicacion();
        s.setId(rs.getInt("id_stock"));
        s.setIdProducto(rs.getInt("id_producto"));
        s.setUbicacion(Ubicacion.valueOf(rs.getString("ubicacion")));
        s.setCantidad(rs.getInt("cantidad"));
        return s;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
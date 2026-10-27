package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.DetalleVentaDAO;
import com.taller.app.modelo.DetalleVenta;
import com.taller.app.util.ConexionBD;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public void crear(DetalleVenta detalle) {
        String sql = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, detalle.getIdVenta());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecioUnitario());
            ps.setDouble(5, detalle.getSubtotal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear detalle de venta", e);
        }
    }

    @Override
    public List<DetalleVenta> listarPorVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_venta WHERE id_venta = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalle de venta", e);
        }
        return lista;
    }

    private DetalleVenta mapear(ResultSet rs) throws SQLException {
        DetalleVenta d = new DetalleVenta();
        d.setId(rs.getInt("id_detalle"));
        d.setIdVenta(rs.getInt("id_venta"));
        d.setIdProducto(rs.getInt("id_producto"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getDouble("precio_unitario"));
        d.setSubtotal(rs.getDouble("subtotal"));
        return d;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
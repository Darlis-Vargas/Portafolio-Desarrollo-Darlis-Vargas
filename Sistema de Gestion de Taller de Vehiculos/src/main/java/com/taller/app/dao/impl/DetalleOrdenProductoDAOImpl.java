package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.DetalleOrdenProductoDAO;
import com.taller.app.modelo.DetalleOrdenProducto;
import com.taller.app.util.ConexionBD;

public class DetalleOrdenProductoDAOImpl implements DetalleOrdenProductoDAO {

    @Override
    public void crear(DetalleOrdenProducto detalle) {
        String sql = "INSERT INTO detalle_orden_productos (id_orden, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, detalle.getIdOrden());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecioUnitario());
            ps.setDouble(5, detalle.getSubtotal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear detalle de orden producto", e);
        }
    }

    @Override
    public List<DetalleOrdenProducto> listarPorOrden(int idOrden) {
        List<DetalleOrdenProducto> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_orden_productos WHERE id_orden = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalle de orden producto", e);
        }
        return lista;
    }

    private DetalleOrdenProducto mapear(ResultSet rs) throws SQLException {
        DetalleOrdenProducto d = new DetalleOrdenProducto();
        d.setId(rs.getInt("id_detalle"));
        d.setIdOrden(rs.getInt("id_orden"));
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
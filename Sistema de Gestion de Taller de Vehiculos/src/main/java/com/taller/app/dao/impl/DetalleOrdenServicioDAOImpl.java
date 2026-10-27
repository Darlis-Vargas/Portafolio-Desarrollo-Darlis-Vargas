package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.DetalleOrdenServicioDAO;
import com.taller.app.modelo.DetalleOrdenServicio;
import com.taller.app.util.ConexionBD;

public class DetalleOrdenServicioDAOImpl implements DetalleOrdenServicioDAO {

    @Override
    public void crear(DetalleOrdenServicio detalle) {
        String sql = "INSERT INTO detalle_orden_servicios (id_orden, id_servicio, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, detalle.getIdOrden());
            ps.setInt(2, detalle.getIdServicio());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecioUnitario());
            ps.setDouble(5, detalle.getSubtotal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear detalle de orden servicio", e);
        }
    }

    @Override
    public List<DetalleOrdenServicio> listarPorOrden(int idOrden) {
        List<DetalleOrdenServicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_orden_servicios WHERE id_orden = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar detalle de orden servicio", e);
        }
        return lista;
    }

    private DetalleOrdenServicio mapear(ResultSet rs) throws SQLException {
        DetalleOrdenServicio d = new DetalleOrdenServicio();
        d.setId(rs.getInt("id_detalle"));
        d.setIdOrden(rs.getInt("id_orden"));
        d.setIdServicio(rs.getInt("id_servicio"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getDouble("precio_unitario"));
        d.setSubtotal(rs.getDouble("subtotal"));
        return d;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}

package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.VentaDAO;
import com.taller.app.modelo.Venta;
import com.taller.app.util.ConexionBD;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public Venta buscarPorId(int id) {
        String sql = "SELECT * FROM ventas WHERE id_venta = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta por id", e);
        }
        return null;
    }

    @Override
    public List<Venta> listarPorFecha(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE fecha BETWEEN ? AND ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(hasta.atTime(23, 59, 59)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas por fecha", e);
        }
        return ventas;
    }

    @Override
    public void crear(Venta venta) {
        String sql = "INSERT INTO ventas (id_usuario_cajero, id_cliente, fecha, total, metodo_pago) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, venta.getIdUsuarioCajero());
            if (venta.getIdCliente() != 0) {
                ps.setInt(2, venta.getIdCliente());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(3, venta.getFecha() != null ? Timestamp.valueOf(venta.getFecha()) : new Timestamp(System.currentTimeMillis()));
            ps.setDouble(4, venta.getTotal());
            ps.setString(5, venta.getMetodoPago());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear venta", e);
        }
    }

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id_venta"));
        v.setIdUsuarioCajero(rs.getInt("id_usuario_cajero"));
        v.setIdCliente(rs.getInt("id_cliente"));
        Timestamp ts = rs.getTimestamp("fecha");
        v.setFecha(ts != null ? ts.toLocalDateTime() : null);
        v.setTotal(rs.getDouble("total"));
        v.setMetodoPago(rs.getString("metodo_pago"));
        return v;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
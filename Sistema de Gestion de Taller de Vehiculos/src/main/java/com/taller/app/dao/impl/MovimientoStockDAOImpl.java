package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.MovimientoStockDAO;
import com.taller.app.modelo.MovimientoStock;
import com.taller.app.modelo.Ubicacion;
import com.taller.app.util.ConexionBD;

public class MovimientoStockDAOImpl implements MovimientoStockDAO {

    @Override
    public void crear(MovimientoStock movimiento) {
        String sql = "INSERT INTO movimientos_stock (id_producto, ubicacion_origen, ubicacion_destino, cantidad, id_usuario, fecha, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, movimiento.getIdProducto());
            if (movimiento.getUbicacionOrigen() != null) {
                ps.setString(2, movimiento.getUbicacionOrigen().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }
            if (movimiento.getUbicacionDestino() != null) {
                ps.setString(3, movimiento.getUbicacionDestino().name());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }
            ps.setInt(4, movimiento.getCantidad());
            ps.setInt(5, movimiento.getIdUsuario());
            ps.setTimestamp(6, movimiento.getFecha() != null
                    ? Timestamp.valueOf(movimiento.getFecha())
                    : new Timestamp(System.currentTimeMillis()));
            ps.setString(7, movimiento.getTipo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear movimiento de stock", e);
        }
    }

    @Override
    public List<MovimientoStock> listarPorProducto(int idProducto) {
        List<MovimientoStock> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimientos_stock WHERE id_producto = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar movimientos por producto", e);
        }
        return lista;
    }

    private MovimientoStock mapear(ResultSet rs) throws SQLException {
        MovimientoStock m = new MovimientoStock();
        m.setId(rs.getInt("id_movimiento"));
        m.setIdProducto(rs.getInt("id_producto"));
        String origen = rs.getString("ubicacion_origen");
        m.setUbicacionOrigen(origen != null ? Ubicacion.valueOf(origen) : null);
        String destino = rs.getString("ubicacion_destino");
        m.setUbicacionDestino(destino != null ? Ubicacion.valueOf(destino) : null);
        m.setCantidad(rs.getInt("cantidad"));
        m.setIdUsuario(rs.getInt("id_usuario"));
        Timestamp ts = rs.getTimestamp("fecha");
        m.setFecha(ts != null ? ts.toLocalDateTime() : null);
        m.setTipo(rs.getString("tipo"));
        return m;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
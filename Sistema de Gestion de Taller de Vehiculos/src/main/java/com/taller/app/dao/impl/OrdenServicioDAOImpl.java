package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.OrdenServicioDAO;
import com.taller.app.modelo.EstadoOrden;
import com.taller.app.modelo.OrdenServicio;
import com.taller.app.util.ConexionBD;

public class OrdenServicioDAOImpl implements OrdenServicioDAO {

    @Override
    public OrdenServicio buscarPorId(int id) {
        String sql = "SELECT * FROM ordenes_servicio WHERE id_orden = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar orden por id", e);
        }
        return null;
    }

    @Override
    public List<OrdenServicio> listarPorVehiculo(int idVehiculo) {
        List<OrdenServicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_servicio WHERE id_vehiculo = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idVehiculo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ordenes por vehiculo", e);
        }
        return lista;
    }

    @Override
    public List<OrdenServicio> listarPorEstado(EstadoOrden estado) {
        List<OrdenServicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_servicio WHERE estado = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ordenes por estado", e);
        }
        return lista;
    }

    @Override
    public List<OrdenServicio> listarPorTecnico(int idUsuarioTecnico) {
        List<OrdenServicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_servicio WHERE id_usuario_tecnico = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuarioTecnico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ordenes por tecnico", e);
        }
        return lista;
    }
    
    @Override
    public List<OrdenServicio> listar() {
        List<OrdenServicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordenes_servicio";
        try (PreparedStatement ps = getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ordenes", e);
        }
        return lista;
    }
    

    @Override
    public void crear(OrdenServicio orden) {
        String sql = "INSERT INTO ordenes_servicio (id_vehiculo, id_cliente, id_usuario_recibe, id_usuario_tecnico, fecha_ingreso, fecha_entrega_estimada, fecha_entrega_real, estado, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, orden.getIdVehiculo());
            ps.setInt(2, orden.getIdCliente());
            ps.setInt(3, orden.getIdUsuarioRecibe());
            if (orden.getIdUsuarioTecnico() != 0) {
                ps.setInt(4, orden.getIdUsuarioTecnico());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(5, orden.getFechaIngreso() != null ? Timestamp.valueOf(orden.getFechaIngreso()) : new Timestamp(System.currentTimeMillis()));
            ps.setTimestamp(6, orden.getFechaEntregaEstimada() != null ? Timestamp.valueOf(orden.getFechaEntregaEstimada()) : null);
            ps.setTimestamp(7, orden.getFechaEntregaReal() != null ? Timestamp.valueOf(orden.getFechaEntregaReal()) : null);
            ps.setString(8, orden.getEstado() != null ? orden.getEstado().name() : EstadoOrden.RECIBIDO.name());
            ps.setDouble(9, orden.getTotal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear orden de servicio", e);
        }
    }

    @Override
    public void actualizar(OrdenServicio orden) {
        String sql = "UPDATE ordenes_servicio SET id_usuario_tecnico = ?, fecha_entrega_estimada = ?, fecha_entrega_real = ?, estado = ?, total = ? WHERE id_orden = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            if (orden.getIdUsuarioTecnico() != 0) {
                ps.setInt(1, orden.getIdUsuarioTecnico());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(2, orden.getFechaEntregaEstimada() != null ? Timestamp.valueOf(orden.getFechaEntregaEstimada()) : null);
            ps.setTimestamp(3, orden.getFechaEntregaReal() != null ? Timestamp.valueOf(orden.getFechaEntregaReal()) : null);
            ps.setString(4, orden.getEstado().name());
            ps.setDouble(5, orden.getTotal());
            ps.setInt(6, orden.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar orden de servicio", e);
        }
    }

    private OrdenServicio mapear(ResultSet rs) throws SQLException {
        OrdenServicio o = new OrdenServicio();
        o.setId(rs.getInt("id_orden"));
        o.setIdVehiculo(rs.getInt("id_vehiculo"));
        o.setIdCliente(rs.getInt("id_cliente"));
        o.setIdUsuarioRecibe(rs.getInt("id_usuario_recibe"));
        o.setIdUsuarioTecnico(rs.getInt("id_usuario_tecnico"));
        Timestamp ingreso = rs.getTimestamp("fecha_ingreso");
        o.setFechaIngreso(ingreso != null ? ingreso.toLocalDateTime() : null);
        Timestamp estimada = rs.getTimestamp("fecha_entrega_estimada");
        o.setFechaEntregaEstimada(estimada != null ? estimada.toLocalDateTime() : null);
        Timestamp real = rs.getTimestamp("fecha_entrega_real");
        o.setFechaEntregaReal(real != null ? real.toLocalDateTime() : null);
        o.setEstado(EstadoOrden.valueOf(rs.getString("estado")));
        o.setTotal(rs.getDouble("total"));
        return o;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}

package com.taller.app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.taller.app.dao.ProductoDAO;
import com.taller.app.modelo.Producto;
import com.taller.app.util.ConexionBD;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por id", e);
        }
        return null;
    }

    @Override
    public List<Producto> listar() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (PreparedStatement ps = getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos", e);
        }
        return productos;
    }

    @Override
    public List<Producto> listarPorCategoria(int idCategoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE id_categoria = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos por categoria", e);
        }
        return productos;
    }

    @Override
    public void crear(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, id_categoria, precio_venta, costo, stock_minimo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioVenta());
            ps.setDouble(5, producto.getCosto());
            ps.setInt(6, producto.getStockMinimo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear producto", e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, id_categoria = ?, precio_venta = ?, costo = ?, stock_minimo = ? WHERE id_producto = ?";
        try (PreparedStatement ps = getConexion().prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioVenta());
            ps.setDouble(5, producto.getCosto());
            ps.setInt(6, producto.getStockMinimo());
            ps.setInt(7, producto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setCosto(rs.getDouble("costo"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        return p;
    }

    private Connection getConexion() {
        return ConexionBD.obtenerInstancia().getConexion();
    }
}
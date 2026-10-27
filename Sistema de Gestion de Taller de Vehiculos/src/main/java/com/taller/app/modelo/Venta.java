package com.taller.app.modelo;

public class Venta {

	private int id;
	private int idUsuarioCajero;
	private int idCliente;
	private java.time.LocalDateTime fecha;
	private double total;
	private String metodoPago;
	
	//lista en memoria para ir acumulando los productos del carrito antes de guardar
	private java.util.List<DetalleVenta> items = new java.util.ArrayList<>();

	public java.util.List<DetalleVenta> getItems() {
	    return items;
	}
	
	// Getters and Setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdUsuarioCajero() {
		return idUsuarioCajero;
	}
	public void setIdUsuarioCajero(int idUsuarioCajero) {
		this.idUsuarioCajero = idUsuarioCajero;
	}
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public java.time.LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(java.time.LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getMetodoPago() {
		return metodoPago;
	}
	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}
	
	
}

package com.taller.app.modelo;

public class MovimientoStock {
	
	private int id;
	private int idProducto;
	private Ubicacion ubicacionOrigen;
	private Ubicacion ubicacionDestino;
	private int cantidad;
	private int idUsuario;
	private java.time.LocalDateTime fecha;
	private String tipo;
	
	
	// Getters and Setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}
	public Ubicacion getUbicacionOrigen() {
		return ubicacionOrigen;
	}
	public void setUbicacionOrigen(Ubicacion ubicacionOrigen) {
		this.ubicacionOrigen = ubicacionOrigen;
	}
	public Ubicacion getUbicacionDestino() {
		return ubicacionDestino;
	}
	public void setUbicacionDestino(Ubicacion ubicacionDestino) {
		this.ubicacionDestino = ubicacionDestino;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public java.time.LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(java.time.LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	

}

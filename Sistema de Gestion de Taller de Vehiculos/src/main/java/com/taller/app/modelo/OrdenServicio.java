package com.taller.app.modelo;

public class OrdenServicio {
	
	private int id;
	private int idVehiculo;
	private int idCliente;
	private int idUsuarioRecibe;
	private int idUsuarioTecnico;
	private java.time.LocalDateTime fechaIngreso;
	private java.time.LocalDateTime fechaEntregaEstimada;
	private java.time.LocalDateTime fechaEntregaReal;
	private EstadoOrden estado;
	private double total;
	
	
	// Getters and Setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdVehiculo() {
		return idVehiculo;
	}
	public void setIdVehiculo(int idVehiculo) {
		this.idVehiculo = idVehiculo;
	}
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public int getIdUsuarioRecibe() {
		return idUsuarioRecibe;
	}
	public void setIdUsuarioRecibe(int idUsuarioRecibe) {
		this.idUsuarioRecibe = idUsuarioRecibe;
	}
	public int getIdUsuarioTecnico() {
		return idUsuarioTecnico;
	}
	public void setIdUsuarioTecnico(int idUsuarioTecnico) {
		this.idUsuarioTecnico = idUsuarioTecnico;
	}
	public java.time.LocalDateTime getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(java.time.LocalDateTime fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
	public java.time.LocalDateTime getFechaEntregaEstimada() {
		return fechaEntregaEstimada;
	}
	public void setFechaEntregaEstimada(java.time.LocalDateTime fechaEntregaEstimada) {
		this.fechaEntregaEstimada = fechaEntregaEstimada;
	}
	public java.time.LocalDateTime getFechaEntregaReal() {
		return fechaEntregaReal;
	}
	public void setFechaEntregaReal(java.time.LocalDateTime fechaEntregaReal) {
		this.fechaEntregaReal = fechaEntregaReal;
	}
	public EstadoOrden getEstado() {
		return estado;
	}
	public void setEstado(EstadoOrden estado) {
		this.estado = estado;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	

}

package com.taller.app.modelo;

public class Servicio {

	private int id;
	private String nombre;
	private String descripcion;
	private double precioBase;
	private int duracionEstimada;
	
	
	// Getters and Setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public double getPrecioBase() {
		return precioBase;
	}
	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}
	public int getDuracionEstimada() {
		return duracionEstimada;
	}
	public void setDuracionEstimada(int duracionEstimada) {
		this.duracionEstimada = duracionEstimada;
	}
	
	
	
}

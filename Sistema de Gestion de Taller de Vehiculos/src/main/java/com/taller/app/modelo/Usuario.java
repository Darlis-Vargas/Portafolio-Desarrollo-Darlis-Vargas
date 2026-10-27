package com.taller.app.modelo;

public class Usuario {

	private int id;
	private String nombre;
	private String usuario;
	private String passwordHash;
	private Rol rol;
	private boolean activo;
	
	
	public Usuario() {
	}

	public Usuario(String nombre, String usuario, String passwordHash, Rol rol) {
	    this.nombre = nombre;
	    this.usuario = usuario;
	    this.passwordHash = passwordHash;
	    this.rol = rol;
	    this.activo = true;
	}
	
	// getters and setters
	
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
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	public Rol getRol() {
		return rol;
	}
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
	
	
}

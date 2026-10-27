package com.taller.app.servicio;

import com.taller.app.modelo.Rol;
import com.taller.app.modelo.Usuario;

public class Sesion {

	private static Sesion instancia;
    private Usuario usuarioActivo;

    private Sesion() {
    }

    public static Sesion obtenerInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActivo = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActivo = null;
    }

    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    public boolean tienePermiso(String modulo) {
        if (usuarioActivo == null) {
            return false;
        }
        Rol rol = usuarioActivo.getRol();

        switch (modulo) {
            case "GESTION_USUARIOS":
                return rol == Rol.ADMINISTRADOR;
            case "VER_INVENTARIO":
                return true; // todos ven algo; cada pantalla filtra tienda/taller
            case "MOVER_STOCK":
                return rol == Rol.ADMINISTRADOR || rol == Rol.RECEPCION_TALLER;
            case "REGISTRAR_VENTAS":
                return rol == Rol.ADMINISTRADOR || rol == Rol.CAJERO;
            case "CREAR_ORDEN":
                return rol == Rol.ADMINISTRADOR || rol == Rol.RECEPCION_TALLER;
            case "ACTUALIZAR_ORDEN":
                return rol == Rol.ADMINISTRADOR || rol == Rol.RECEPCION_TALLER || rol == Rol.TECNICO;
            case "VER_REPORTES":
                return true; // cada rol ve un subconjunto, se filtra en ReportesServicio
            default:
                return false;
        }
    }
}
package com.taller.app.util;

public class Validaciones {

	 public static boolean esVacio(String texto) {
	        return texto == null || texto.trim().isEmpty();
	    }

	    public static boolean esEmailValido(String email) {
	        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$");
	    }

	    public static boolean esCantidadValida(int cantidad) {
	        return cantidad > 0;
	    }

	    public static boolean esPrecioValido(double precio) {
	        return precio >= 0;
	    }
	}
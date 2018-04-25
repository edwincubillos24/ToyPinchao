package com.edwinacubillos.toypinchao.model;

public class Montallantas {
    String foto, nombre, valor;

    public Montallantas() {
    }

    public Montallantas(String foto, String nombre, String valor) {
        this.foto = foto;
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}

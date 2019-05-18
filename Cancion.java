package com.foxsing;


import java.io.Serializable;



public class Cancion  implements Serializable {
    private String nombre;
    private String artista;
    private String albun;
        private String ruta;
    private int imagen;
    private String id;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Cancion() {
        imagen=0;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public int getImagen() {
        return imagen;
    }

    public String getAlbun() {
        return albun;
    }

    public void setAlbun(String albun) {
        this.albun = albun;
    }

}



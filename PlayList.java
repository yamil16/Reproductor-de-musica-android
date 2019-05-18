package com.foxsing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PlayList   implements Serializable {
    private   String id;
    private String nombre;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }





    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Modelo de dominio para una Tarea.
 * Representa la entidad que guardamos en BD.
 * 
 * @author Tatiana
 */
public class Tarea {
    private int id; // 0 si aun no fue persistida
    private String titulo;
    private int prioridad; // 1=Alta,2=Media,3=Baja
    private boolean estado; // false = Pendiente, true = Hecho
    private boolean especial; // marca especial ★
    private LocalDate fecha; // puede ser null

    public Tarea() {}

    public Tarea(int id, String titulo, int prioridad, boolean estado, boolean especial, LocalDate fecha) {
        this.id = id;
        this.titulo = titulo;
        this.prioridad = prioridad;
        this.estado = estado;
        this.especial = especial;
        this.fecha = fecha;
    }

    public Tarea(String titulo, int prioridad, boolean especial, LocalDate fecha) {
        this(0, titulo, prioridad, false, especial, fecha);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isEspecial() {
        return especial;
    }

    public void setEspecial(boolean especial) {
        this.especial = especial;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return String.format("Tarea{id=%d, titulo='%s', prioridad=%d, estado=%s, especial=%s, fecha=%s}",
                id, titulo, prioridad, estado ? "Hecho" : "Pendiente", especial ? "★" : "-", fecha);
    }
}
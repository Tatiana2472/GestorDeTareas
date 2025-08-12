/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import java.sql.SQLException;
import dao.TareaDAO;
import dominio.Tarea;
import excepciones.DatoInvalidoException;
import java.time.LocalDate;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

/**
 Capa de servicio/negocio. Mantiene:
 * - cache en memoria (ArrayList) para operaciones UI rápidas
 * - pila (Stack<Integer>) para deshacer la última eliminación (almacena ids)
 * Validaciones y transformación de datos antes de llamar al DAO.
 * 
 * @author Tatiana
 */
public class TareaServicio {
    private final TareaDAO dao;
    private final List<Tarea> cache; // ArrayList como estructura dinámica
    private final Stack<Integer> pilaEliminados; // Stack para deshacer

    public TareaServicio() {
        this.dao = new TareaDAO();
        this.cache = new ArrayList<>();
        this.pilaEliminados = new Stack<>();
    }

    // Carga inicial desde la BD
    public void cargarDesdeBD() throws SQLException {
        cache.clear();
        cache.addAll(dao.listar());
    }

    // Retorna una copia de la lista (para UI)
    public List<Tarea> obtenerTareas() {
        return new ArrayList<>(cache);
    }

    // Validaciones y creación
    public Tarea crearTarea(String titulo, int prioridad, boolean especial, LocalDate fecha) throws DatoInvalidoException, SQLException {
        titulo = (titulo != null) ? titulo.trim() : "";
        if (titulo.isEmpty()) {
            throw new DatoInvalidoException("El título no puede estar vacío.");
        }
        if (prioridad < 1 || prioridad > 3) {
            throw new DatoInvalidoException("La prioridad debe ser 1 (Alta), 2 (Media) o 3 (Baja).");
        }

        Tarea t = new Tarea(titulo, prioridad, especial, fecha);
        Tarea insertada = dao.insertar(t);
        cache.add(insertada);
        return insertada;
    }

    // Alternar estado: actualiza cache y BD
    public void alternarEstado(int id) throws SQLException {
        for (Tarea t : cache) {
            if (t.getId() == id) {
                boolean nuevo = !t.isEstado();
                t.setEstado(nuevo);
                dao.actualizarEstado(id, nuevo);
                return;
            }
        }
    }

    // Eliminación lógica: marca en BD, quita de cache y guarda en pila para undo
    public void eliminarLogico(int id) throws SQLException {
        // Guardamos el id en la pila antes de borrar de cache
        dao.eliminarLogico(id);
        // Eliminamos de cache local
        cache.removeIf(t -> t.getId() == id);
        // Apilamos el id para deshacer más tarde
        pilaEliminados.push(id);
        // Límite razonable (por ejemplo 20)
        if (pilaEliminados.size() > 20) {
            pilaEliminados.remove(0); // mantener tamaño razonable (podrías usar LinkedList si prefieres)
        }
    }

    // Deshacer la última eliminación
    public boolean deshacerUltimaEliminacion() throws SQLException {
        if (pilaEliminados.isEmpty()) {
            return false;
        }
        int id = pilaEliminados.pop();
        dao.restaurar(id);
        // recargar desde BD el registro restaurado (simple y seguro: recargar toda la lista)
        cargarDesdeBD();
        return true;
    }
}

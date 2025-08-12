/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dominio.Tarea;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO con operaciones CRUD (real) utilizando PreparedStatement y transacciones donde aplica.
 * - listar() devuelve solo las tareas visibles (visible = true).
 * - insertar() devuelve el id generado y lo asigna al objeto Tarea.
 * - eliminarLogico() marca visible = false (operación atómica).
 * - restaurar() vuelve visible = true.
 * - actualizarEstado() actualiza el campo estado.
 * 
 * @author Tatiana
 */
public class TareaDAO {
    // Inserta la tarea y setea el id generado en el objeto
    public Tarea insertar(Tarea t) throws SQLException {
        String sql = "INSERT INTO tarea(titulo, prioridad, estado, especial, fecha, visible, creado_en) VALUES (?, ?, ?, ?, ?, true, CURRENT_TIMESTAMP)";
        try (Connection conn = (Connection) ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getTitulo());
            ps.setInt(2, t.getPrioridad());
            ps.setBoolean(3, t.isEstado());
            ps.setBoolean(4, t.isEspecial());
            if (t.getFecha() != null) {
                ps.setDate(5, Date.valueOf(t.getFecha()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    t.setId(id);
                }
            }
            return t;
        }
    }

    // Lista solo las tareas visibles
    public List<Tarea> listar() throws SQLException {
        String sql = "SELECT id, titulo, prioridad, estado, especial, fecha FROM tarea WHERE visible = true ORDER BY id";
        List<Tarea> lista = new ArrayList<>();
        try (Connection conn = (Connection) ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String titulo = rs.getString("titulo");
                int prioridad = rs.getInt("prioridad");
                boolean estado = rs.getBoolean("estado");
                boolean especial = rs.getBoolean("especial");
                Date fechaSql = rs.getDate("fecha");
                LocalDate fecha = (fechaSql != null) ? fechaSql.toLocalDate() : null;

                lista.add(new Tarea(id, titulo, prioridad, estado, especial, fecha));
            }
        }
        return lista;
    }

    // Eliminación lógica (visible = false) con transacción simple
    public void eliminarLogico(int id) throws SQLException {
        String sql = "UPDATE tarea SET visible = false, actualizado_en = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = (Connection) ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            ps.setInt(1, id);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    // Restaurar una tarea (visible = true)
    public void restaurar(int id) throws SQLException {
        String sql = "UPDATE tarea SET visible = true, actualizado_en = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = (Connection) ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            ps.setInt(1, id);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    // Actualizar estado (Hecho/Pendiente)
    public void actualizarEstado(int id, boolean nuevoEstado) throws SQLException {
        String sql = "UPDATE tarea SET estado = ?, actualizado_en = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = (Connection) ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}

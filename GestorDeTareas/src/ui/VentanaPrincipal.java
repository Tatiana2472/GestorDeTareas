/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import dominio.Tarea;
import excepciones.DatoInvalidoException;
import servicio.TareaServicio;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
* Ventana principal (Swing) — simple, funcional y comentada.
 * - No usa hilos complicados porque las operaciones son rápidas; si tu BD es remota,
 *   considera usar SwingWorker para no bloquear el EDT.
 * 
 * @author tatia
 */
public class VentanaPrincipal extends JFrame {

    private final TareaServicio servicio;

    // Componentes UI
    private JTextField txtTitulo;
    private JComboBox<String> cbPrioridad;
    private JCheckBox chkEspecial;
    private JButton btnAgregar, btnAlternar, btnEliminar, btnDeshacer, btnRefrescar;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblEstado;
    private JSpinner spinnerFecha;

    // Colores personalizados
    private final Color colorPrincipal = new Color(45, 118, 232); // azul vibrante
    private final Color colorSecundario = new Color(255, 140, 0); // naranja llamativo
    private final Color colorFondo = new Color(245, 247, 250); // fondo muy claro
    private final Color colorTextoOscuro = new Color(33, 33, 33);

    // Fuentes elegantes
    private final Font fontTitulo = new Font("Segoe UI Semibold", Font.BOLD, 18);
    private final Font fontLabels = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font fontBotones = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fontEstado = new Font("Segoe UI Italic", Font.PLAIN, 12);

    public VentanaPrincipal() {
        super("Gestor de Tareas - Tatiana");
        this.servicio = new TareaServicio();
        initComponents();
        cargarDatosIniciales();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(colorFondo);

        // PANEL FORMULARIO - Fondo blanco y borde redondeado
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(colorPrincipal, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(fontLabels);
        lblTitulo.setForeground(colorTextoOscuro);
        panelForm.add(lblTitulo, gbc);

        txtTitulo = new JTextField(28);
        txtTitulo.setFont(fontLabels);
        txtTitulo.setForeground(colorTextoOscuro);
        gbc.gridx = 1;
        panelForm.add(txtTitulo, gbc);

        // Prioridad
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPrioridad = new JLabel("Prioridad:");
        lblPrioridad.setFont(fontLabels);
        lblPrioridad.setForeground(colorTextoOscuro);
        panelForm.add(lblPrioridad, gbc);

        cbPrioridad = new JComboBox<>(new String[] {"1 - Alta", "2 - Media", "3 - Baja"});
        cbPrioridad.setFont(fontLabels);
        cbPrioridad.setForeground(colorTextoOscuro);
        gbc.gridx = 1;
        panelForm.add(cbPrioridad, gbc);

        // Fecha con JSpinner
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(fontLabels);
        lblFecha.setForeground(colorTextoOscuro);
        panelForm.add(lblFecha, gbc);

        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFecha, "yyyy-MM-dd");
        spinnerFecha.setEditor(dateEditor);
        spinnerFecha.setValue(new Date());
        spinnerFecha.setFont(fontLabels);
        gbc.gridx = 1;
        panelForm.add(spinnerFecha, gbc);

        // Especial
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblEspecial = new JLabel("Especial (★):");
        lblEspecial.setFont(fontLabels);
        lblEspecial.setForeground(colorTextoOscuro);
        panelForm.add(lblEspecial, gbc);

        chkEspecial = new JCheckBox("Marcar como especial");
        chkEspecial.setFont(fontLabels);
        chkEspecial.setForeground(colorPrincipal);
        chkEspecial.setBackground(Color.WHITE);
        gbc.gridx = 1;
        panelForm.add(chkEspecial, gbc);

        // Botones de agregar y refrescar
        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBtns.setBackground(Color.WHITE);

        btnAgregar = crearBotonPersonalizado("Agregar", colorPrincipal, Color.WHITE);
        btnRefrescar = crearBotonPersonalizado("Refrescar", colorSecundario, Color.WHITE);
        panelBtns.add(btnAgregar);
        panelBtns.add(btnRefrescar);

        gbc.gridx = 1; gbc.gridy = 4;
        panelForm.add(panelBtns, gbc);

        add(panelForm, BorderLayout.NORTH);

        // TABLA
        modeloTabla = new DefaultTableModel(
                new String[] {"ID", "Título", "Prioridad", "Estado", "Especial", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionBackground(colorSecundario);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFont(fontLabels);
        tabla.getTableHeader().setFont(fontTitulo);
        tabla.getTableHeader().setBackground(colorPrincipal);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(colorPrincipal, 2, true));
        add(scroll, BorderLayout.CENTER);

        // PANEL INFERIOR CON BOTONES Y ESTADO
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        panelInferior.setBackground(colorFondo);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panelAcciones.setBackground(colorFondo);

        btnAlternar = crearBotonPersonalizado("Alternar Hecho", colorPrincipal, Color.WHITE);
        btnEliminar = crearBotonPersonalizado("Eliminar (ocultar)", colorSecundario, Color.WHITE);
        btnDeshacer = crearBotonPersonalizado("Deshacer última eliminación", colorPrincipal.darker(), Color.WHITE);

        panelAcciones.add(btnAlternar);
        panelAcciones.add(btnEliminar);
        panelAcciones.add(btnDeshacer);

        panelInferior.add(panelAcciones, BorderLayout.NORTH);

        lblEstado = new JLabel("Estado: listo");
        lblEstado.setFont(fontEstado);
        lblEstado.setForeground(colorTextoOscuro);
        panelInferior.add(lblEstado, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        // LISTENERS (igual que antes)
        btnAgregar.addActionListener(e -> onAgregar());
        btnAlternar.addActionListener(e -> onAlternar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnDeshacer.addActionListener(e -> onDeshacer());
        btnRefrescar.addActionListener(e -> refrescarTabla());

        txtTitulo.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onAgregar();
                }
            }
        });
    }

    // Método para crear botones personalizados con hover
    private JButton crearBotonPersonalizado(String texto, Color bgColor, Color fgColor) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setBackground(bgColor);
        boton.setForeground(fgColor);
        boton.setFont(fontBotones);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(bgColor);
            }
        });

        return boton;
    }

    private void cargarDatosIniciales() {
        try {
            servicio.cargarDesdeBD();
            refrescarTabla();
        } catch (SQLException ex) {
            mostrarMensaje("Error al cargar datos desde la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Tarea> lista = servicio.obtenerTareas();
            for (Tarea t : lista) {
                modeloTabla.addRow(new Object[]{
                        t.getId(),
                        t.getTitulo(),
                        t.getPrioridad(),
                        t.isEstado() ? "Hecho" : "Pendiente",
                        t.isEspecial() ? "★" : "",
                        (t.getFecha() != null) ? t.getFecha().toString() : ""
                });
            }
            lblEstado.setText("Estado: datos actualizados (" + lista.size() + " tareas)");
        } catch (Exception ex) {
            mostrarMensaje("Error al refrescar tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAgregar() {
        String titulo = txtTitulo.getText().trim();
        int prioridad = cbPrioridad.getSelectedIndex() + 1;
        boolean especial = chkEspecial.isSelected();

        Date fechaSeleccionada = (Date) spinnerFecha.getValue();
        LocalDate fecha = fechaSeleccionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            servicio.crearTarea(titulo, prioridad, especial, fecha);
            limpiarForm();
            refrescarTabla();
            mostrarMensaje("Tarea agregada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            lblEstado.setText("Estado: tarea agregada");
        } catch (DatoInvalidoException ex) {
            mostrarMensaje(ex.getMessage(), "Error de validación", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarMensaje("Error al guardar en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAlternar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            mostrarMensaje("Por favor, selecciona una tarea en la tabla para alternar su estado.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String titulo = (String) modeloTabla.getValueAt(fila, 1);
        String estadoActual = (String) modeloTabla.getValueAt(fila, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Cambiar el estado de la tarea \"" + titulo + "\" de " + estadoActual + "?",
                "Confirmar cambio de estado", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            servicio.alternarEstado(id);
            refrescarTabla();
            mostrarMensaje("Estado de la tarea actualizado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            lblEstado.setText("Estado: tarea actualizada");
        } catch (SQLException ex) {
            mostrarMensaje("Error al actualizar estado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            lblEstado.setText("Estado: error al actualizar");
        }
    }

    private void onEliminar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            mostrarMensaje("Selecciona una tarea en la tabla para eliminar.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar (ocultar) la tarea seleccionada?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;

        try {
            servicio.eliminarLogico(id);
            refrescarTabla();
            mostrarMensaje("Tarea eliminada (oculta) correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            lblEstado.setText("Estado: tarea eliminada (oculta)");
        } catch (SQLException ex) {
            mostrarMensaje("Error al eliminar tarea: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeshacer() {
        try {
            boolean hecho = servicio.deshacerUltimaEliminacion();
            if (hecho) {
                refrescarTabla();
                mostrarMensaje("Última eliminación deshecha correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                lblEstado.setText("Estado: última eliminación deshecha");
            } else {
                mostrarMensaje("No hay eliminaciones para deshacer.",
                        "Atención", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            mostrarMensaje("Error al deshacer eliminación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarForm() {
        txtTitulo.setText("");
        cbPrioridad.setSelectedIndex(1);
        spinnerFecha.setValue(new Date());
        chkEspecial.setSelected(false);
    }

    private void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
        if (tipo == JOptionPane.ERROR_MESSAGE) {
            lblEstado.setText("Estado: error");
            System.err.println(mensaje);
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestordetareas;

import ui.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada: ejecuta la GUI en el EDT.
 * @author Tatiana
 */
public class GestorDeTareas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vp = new VentanaPrincipal();
            vp.setVisible(true);
        });
    }
}

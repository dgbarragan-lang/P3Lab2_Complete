package gestion_usuarios;

import Gui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Configurar Look and Feel moderno
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Error al configurar Look and Feel: " + e.getMessage());
        }
        
        // Ejecutar en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
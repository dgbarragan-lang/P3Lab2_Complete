package Gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import DataBase.*;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NuevoUsuarioDialog extends JDialog {
    private UsuarioDAO usuarioDAO;
    private boolean usuarioCreado = false;
    
    private JTextField txtNombre, txtEmail, txtTelefono;
    private JComboBox<Rol> cmbRol;
    private JComboBox<Estado> cmbEstado;
    
    public NuevoUsuarioDialog(JFrame parent, UsuarioDAO usuarioDAO) {
        super(parent, "Crear Nuevo Usuario", true);
        this.usuarioDAO = usuarioDAO;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel de título
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Crear Nuevo Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblSubtitulo = new JLabel("Completa la información del nuevo usuario del sistema");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.GRAY);
        
        titlePanel.add(lblTitulo, BorderLayout.NORTH);
        titlePanel.add(lblSubtitulo, BorderLayout.SOUTH);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNombre = new JTextField(30);
        txtNombre.putClientProperty("JTextField.placeholderText", "Ej: Juan Pérez");
        formPanel.add(txtNombre, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtEmail = new JTextField(30);
        txtEmail.putClientProperty("JTextField.placeholderText", "usuario@ejemplo.com");
        formPanel.add(txtEmail, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTelefono = new JTextField(30);
        txtTelefono.putClientProperty("JTextField.placeholderText", "+34 600 000 000");
        formPanel.add(txtTelefono, gbc);
        
        // Rol
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbRol = new JComboBox<>(Rol.values());
        cmbRol.setSelectedItem(Rol.PACIENTE);
        formPanel.add(cmbRol, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbEstado = new JComboBox<>(Estado.values());
        cmbEstado.setSelectedItem(Estado.ACTIVO);
        formPanel.add(cmbEstado, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnCrear = new JButton("Crear Usuario");
        btnCrear.setBackground(new Color(52, 152, 219));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFocusPainted(false);
        
        btnCancelar.addActionListener(e -> dispose());
        btnCrear.addActionListener(e -> crearUsuario());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnCrear);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Configurar tamaño
        setPreferredSize(new Dimension(500, 400));
    }
    
    private void crearUsuario() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            Usuario nuevoUsuario = new Usuario(
                txtNombre.getText().trim(),
                txtEmail.getText().trim(),
                txtTelefono.getText().trim(),
                (Rol) cmbRol.getSelectedItem(),
                (Estado) cmbEstado.getSelectedItem()
            );
            
            boolean creado = usuarioDAO.agregarUsuario(nuevoUsuario);
            if (creado) {
                usuarioCreado = true;
                JOptionPane.showMessageDialog(this,
                    "Usuario creado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al crear el usuario. El email puede estar duplicado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al crear usuario: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es requerido.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El email es requerido.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        if (!txtEmail.getText().contains("@")) {
            JOptionPane.showMessageDialog(this,
                "El email no es válido.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isUsuarioCreado() {
        return usuarioCreado;
    }
}

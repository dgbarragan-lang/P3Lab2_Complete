package Gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import DataBase.*;
import Model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditarUsuarioDialog extends JDialog {
    private UsuarioDAO usuarioDAO;
    private Usuario usuario;
    private boolean usuarioActualizado = false;
    
    private JTextField txtNombre, txtEmail, txtTelefono;
    private JComboBox<Rol> cmbRol;
    private JComboBox<Estado> cmbEstado;
    private JLabel lblUltimoAcceso;
    
    public EditarUsuarioDialog(JFrame parent, UsuarioDAO usuarioDAO, Usuario usuario) {
        super(parent, "Editar Usuario", true);
        this.usuarioDAO = usuarioDAO;
        this.usuario = usuario;
        initComponents();
        cargarDatosUsuario();
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
        JLabel lblTitulo = new JLabel("Editar Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        titlePanel.add(lblTitulo, BorderLayout.NORTH);
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
        formPanel.add(txtNombre, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtEmail = new JTextField(30);
        formPanel.add(txtEmail, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtTelefono = new JTextField(30);
        formPanel.add(txtTelefono, gbc);
        
        // Rol
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbRol = new JComboBox<>(Rol.values());
        formPanel.add(cmbRol, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbEstado = new JComboBox<>(Estado.values());
        formPanel.add(cmbEstado, gbc);
        
        // Último acceso
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Último Acceso:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        lblUltimoAcceso = new JLabel();
        formPanel.add(lblUltimoAcceso, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarCambios());
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Configurar tamaño
        setPreferredSize(new Dimension(500, 450));
    }
    
    private void cargarDatosUsuario() {
        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        txtTelefono.setText(usuario.getTelefono());
        cmbRol.setSelectedItem(usuario.getRolEnum());
        cmbEstado.setSelectedItem(usuario.getEstadoEnum());
        lblUltimoAcceso.setText(usuario.getUltimoAcceso());
    }
    
    private void guardarCambios() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setTelefono(txtTelefono.getText().trim());
            usuario.setRolEnum((Rol) cmbRol.getSelectedItem());
            usuario.setEstadoEnum((Estado) cmbEstado.getSelectedItem());
            
            boolean actualizado = usuarioDAO.actualizarUsuario(usuario);
            if (actualizado) {
                usuarioActualizado = true;
                JOptionPane.showMessageDialog(this,
                    "Usuario actualizado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el usuario.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar usuario: " + e.getMessage(),
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
    
    public boolean isUsuarioActualizado() {
        return usuarioActualizado;
    }
}

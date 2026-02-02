package Gui;

import DataBase.*;
import Model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class MainFrame extends JFrame {
    private UsuarioDAO usuarioDAO;
    private JLabel lblTotalValor, lblActivosValor, lblDoctoresValor, lblPacientesValor;
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltroRol;
    private JTable tblUsuarios;
    private DefaultTableModel tableModel;
    private List<Usuario> usuariosList;
    
    public MainFrame() {
        usuarioDAO = new UsuarioDAO();
        initComponents();
        cargarDatos();
        aplicarEstilos();
    }
    
    private void initComponents() {
        setTitle("Gestión de Usuarios - Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // PANEL SUPERIOR (Título y estadísticas)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblSubtitulo = new JLabel("Administra usuarios, roles y permisos del sistema");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.GRAY);
        
        titlePanel.add(lblTitulo, BorderLayout.NORTH);
        titlePanel.add(lblSubtitulo, BorderLayout.SOUTH);
        titlePanel.setOpaque(false);
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        
        // Crear las tarjetas de estadísticas
        JPanel totalCard = crearStatCard("Total Usuarios", "0", new Color(52, 152, 219));
        JPanel activosCard = crearStatCard("Usuarios Activos", "0", new Color(46, 204, 113));
        JPanel doctoresCard = crearStatCard("Doctores", "0", new Color(155, 89, 182));
        JPanel pacientesCard = crearStatCard("Pacientes", "0", new Color(241, 196, 15));
        
        // Obtener las referencias a las etiquetas de valores
        lblTotalValor = (JLabel) ((BorderLayout) totalCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        lblActivosValor = (JLabel) ((BorderLayout) activosCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        lblDoctoresValor = (JLabel) ((BorderLayout) doctoresCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        lblPacientesValor = (JLabel) ((BorderLayout) pacientesCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        
        statsPanel.add(totalCard);
        statsPanel.add(activosCard);
        statsPanel.add(doctoresCard);
        statsPanel.add(pacientesCard);
        
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.SOUTH);
        
        // PANEL CENTRAL (Búsqueda y tabla)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);
        
        txtBuscar = new JTextField(30);
        txtBuscar.setPreferredSize(new Dimension(300, 35));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre o email...");
        
        cmbFiltroRol = new JComboBox<>(new String[]{"Todos los roles", "Administrador", "Doctor", "Paciente", "Personal"});
        cmbFiltroRol.setPreferredSize(new Dimension(150, 35));
        
        JButton btnNuevo = new JButton("Nuevo Usuario");
        btnNuevo.setPreferredSize(new Dimension(120, 35));
        btnNuevo.setBackground(new Color(52, 152, 219));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> abrirNuevoUsuario());
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(100, 35));
        btnActualizar.addActionListener(e -> cargarDatos());
        
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(txtBuscar);
        searchPanel.add(new JLabel("Filtrar por rol:"));
        searchPanel.add(cmbFiltroRol);
        searchPanel.add(btnNuevo);
        searchPanel.add(btnActualizar);
        
        // Tabla de usuarios
        String[] columnNames = {"Usuario", "Contacto", "Rol", "Estado", "Último Acceso"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        
        tblUsuarios = new JTable(tableModel);
        tblUsuarios.setRowHeight(60);
        tblUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Configurar renderizadores personalizados
        tblUsuarios.getColumnModel().getColumn(0).setCellRenderer(new UsuarioCellRenderer());
        tblUsuarios.getColumnModel().getColumn(1).setCellRenderer(new ContactoCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(tblUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);
        
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnCambiarEstado = new JButton("🔄 Cambiar Estado");
        
        btnEditar.addActionListener(e -> editarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoUsuario());
        
        actionPanel.add(btnEditar);
        actionPanel.add(btnEliminar);
        actionPanel.add(btnCambiarEstado);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Agregar listeners
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarUsuarios();
            }
        });
        
        cmbFiltroRol.addActionListener(e -> filtrarUsuarios());
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel crearStatCard(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(Color.GRAY);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        
        return card;
    }
    
    private void cargarDatos() {
        try {
            usuariosList = usuarioDAO.getAllUsuarios();
            actualizarTabla(usuariosList);
            
            // Actualizar estadísticas (forma corregida)
            lblTotalValor.setText(String.valueOf(usuarioDAO.getTotalUsuarios()));
            lblTotalValor.setForeground(new Color(52, 152, 219));
            
            lblActivosValor.setText(String.valueOf(usuarioDAO.getUsuariosActivos()));
            lblActivosValor.setForeground(new Color(46, 204, 113));
            
            lblDoctoresValor.setText(String.valueOf(usuarioDAO.getTotalDoctores()));
            lblDoctoresValor.setForeground(new Color(155, 89, 182));
            
            lblPacientesValor.setText(String.valueOf(usuarioDAO.getTotalPacientes()));
            lblPacientesValor.setForeground(new Color(241, 196, 15));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void actualizarTabla(List<Usuario> usuarios) {
        tableModel.setRowCount(0);
        for (Usuario usuario : usuarios) {
            Object[] row = {
                usuario, // Será renderizado por UsuarioCellRenderer
                usuario.getTelefono(), // Será renderizado por ContactoCellRenderer
                usuario.getRol(),
                usuario.getEstado(),
                usuario.getUltimoAcceso()
            };
            tableModel.addRow(row);
        }
    }
    
    private void buscarUsuarios() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarDatos();
        } else {
            usuariosList = usuarioDAO.buscarUsuarios(criterio);
            actualizarTabla(usuariosList);
        }
    }
    
    private void filtrarUsuarios() {
        String rolSeleccionado = (String) cmbFiltroRol.getSelectedItem();
        if (rolSeleccionado.equals("Todos los roles")) {
            cargarDatos();
        } else {
            usuariosList = usuarioDAO.getUsuariosPorRol(rolSeleccionado);
            actualizarTabla(usuariosList);
        }
    }
    
    private void abrirNuevoUsuario() {
        NuevoUsuarioDialog dialog = new NuevoUsuarioDialog(this, usuarioDAO);
        dialog.setVisible(true);
        if (dialog.isUsuarioCreado()) {
            cargarDatos();
        }
    }
    
    private void editarUsuario() {
        int selectedRow = tblUsuarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un usuario para editar.",
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Usuario usuario = usuariosList.get(selectedRow);
        EditarUsuarioDialog dialog = new EditarUsuarioDialog(this, usuarioDAO, usuario);
        dialog.setVisible(true);
        if (dialog.isUsuarioActualizado()) {
            cargarDatos();
        }
    }
    
    private void eliminarUsuario() {
        int selectedRow = tblUsuarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un usuario para eliminar.",
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Usuario usuario = usuariosList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al usuario: " + usuario.getNombre() + "?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = usuarioDAO.eliminarUsuario(usuario.getId());
            if (eliminado) {
                JOptionPane.showMessageDialog(this,
                    "Usuario eliminado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el usuario.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cambiarEstadoUsuario() {
        int selectedRow = tblUsuarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un usuario.",
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Usuario usuario = usuariosList.get(selectedRow);
        Estado nuevoEstado = usuario.getEstadoEnum() == Estado.ACTIVO 
            ? Estado.INACTIVO : Estado.ACTIVO;
        
        usuario.setEstadoEnum(nuevoEstado);
        boolean actualizado = usuarioDAO.actualizarUsuario(usuario);
        
        if (actualizado) {
            JOptionPane.showMessageDialog(this,
                "Estado del usuario actualizado a: " + nuevoEstado.getDescripcion(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar el estado.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarEstilos() {
        // Estilos generales
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Renderizador personalizado para la columna Usuario
    class UsuarioCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Usuario) {
                Usuario usuario = (Usuario) value;
                
                JPanel panel = new JPanel(new BorderLayout(5, 5));
                panel.setOpaque(true);
                panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                
                JLabel nombreLabel = new JLabel(usuario.getNombre());
                nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                emailPanel.setOpaque(false);
                JLabel iconoLabel = new JLabel("☑");
                iconoLabel.setForeground(Color.GRAY);
                JLabel emailLabel = new JLabel(usuario.getEmail());
                emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                emailLabel.setForeground(Color.GRAY);
                
                emailPanel.add(iconoLabel);
                emailPanel.add(emailLabel);
                
                panel.add(nombreLabel, BorderLayout.NORTH);
                panel.add(emailPanel, BorderLayout.SOUTH);
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                return panel;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    // Renderizador personalizado para la columna Contacto
    class ContactoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            JLabel iconoLabel = new JLabel("👩‍⚕️");
            JLabel telefonoLabel = new JLabel(value != null ? value.toString() : "");
            
            panel.add(iconoLabel);
            panel.add(telefonoLabel);
            
            return panel;
        }
    }
}
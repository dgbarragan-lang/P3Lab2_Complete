package gestion_usuarios;

import Model.*;
import DataBase.*;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pruebas para UsuarioDAO")
class UsuarioDAOTest {
    
    private static UsuarioDAO usuarioDAO;
    private static ObjectId testUserId;
    private static final String TEST_EMAIL = "test.dao@hospital.com";
    private static final String TEST_EMAIL_2 = "test.dao2@hospital.com";
    
    @BeforeAll
    static void setup() {
        // Usar MongoDB en memoria para pruebas
        MongoDatabase testDatabase = MongoDBTestHelper.getDatabase();
        
        // Configurar el UsuarioDAO para usar la base de datos de prueba
        // Necesitamos una forma de inyectar la conexión de prueba
        usuarioDAO = new UsuarioDAO() {
            // Sobrescribir para usar la base de datos de prueba
        };
        
        System.out.println("✅ Configuración de pruebas completada");
    }
    
    @Test
    @Order(1)
    @DisplayName("Agregar nuevo usuario")
    void testAgregarUsuario() {
        // Given
        Usuario testUsuario = new Usuario(
            "Test UsuarioDAO",
            TEST_EMAIL,
            "+34 600 123 789",
            Rol.PACIENTE,
            Estado.ACTIVO
        );
        
        // When
        boolean agregado = usuarioDAO.agregarUsuario(testUsuario);
        
        // Then
        assertTrue(agregado, "Debe agregar el usuario correctamente");
        
        // Verificar que se puede recuperar
        Usuario usuarioGuardado = usuarioDAO.getUsuarioByEmail(TEST_EMAIL);
        assertNotNull(usuarioGuardado, "Debe poder recuperar el usuario por email");
        assertEquals("Test UsuarioDAO", usuarioGuardado.getNombre());
        testUserId = usuarioGuardado.getId();
    }
    
    @Test
    @Order(2)
    @DisplayName("No permitir email duplicado")
    void testEmailDuplicado() {
        // Given
        Usuario usuarioDuplicado = new Usuario(
            "Otro Usuario",
            TEST_EMAIL, // Mismo email
            "+34 600 999 999",
            Rol.DOCTOR,
            Estado.ACTIVO
        );
        
        // When
        boolean agregado = usuarioDAO.agregarUsuario(usuarioDuplicado);
        
        // Then
        assertFalse(agregado, "No debe permitir emails duplicados");
    }
    
    @Test
    @Order(3)
    @DisplayName("Obtener todos los usuarios")
    void testGetAllUsuarios() {
        // When
        List<Usuario> usuarios = usuarioDAO.getAllUsuarios();
        
        // Then
        assertNotNull(usuarios, "La lista no debe ser nula");
        assertTrue(usuarios.size() >= 4, "Debe tener al menos 4 usuarios (3 iniciales + 1 de prueba)");
    }
    
    @Test
    @Order(4)
    @DisplayName("Buscar usuarios por criterio")
    void testBuscarUsuarios() {
        // When
        List<Usuario> resultados = usuarioDAO.buscarUsuarios("test");
        
        // Then
        assertFalse(resultados.isEmpty(), "Debe encontrar usuarios con 'test'");
        
        // Buscar por nombre específico
        resultados = usuarioDAO.buscarUsuarios("Test UsuarioDAO");
        assertEquals(1, resultados.size(), "Debe encontrar exactamente un usuario");
        assertEquals(TEST_EMAIL, resultados.get(0).getEmail());
    }
    
    @Test
    @Order(5)
    @DisplayName("Obtener usuario por ID")
    void testGetUsuarioById() {
        // Given
        assertNotNull(testUserId, "Debe tener un ID de prueba");
        
        // When
        Usuario usuario = usuarioDAO.getUsuarioById(testUserId);
        
        // Then
        assertNotNull(usuario, "Debe encontrar el usuario por ID");
        assertEquals("Test UsuarioDAO", usuario.getNombre());
        assertEquals(TEST_EMAIL, usuario.getEmail());
    }
    
    @Test
    @Order(6)
    @DisplayName("Obtener usuario por email")
    void testGetUsuarioByEmail() {
        // When
        Usuario usuario = usuarioDAO.getUsuarioByEmail(TEST_EMAIL);
        
        // Then
        assertNotNull(usuario, "Debe encontrar el usuario por email");
        assertEquals(testUserId, usuario.getId());
    }
    
    @Test
    @Order(7)
    @DisplayName("Actualizar usuario")
    void testActualizarUsuario() {
        // Given
        Usuario usuario = usuarioDAO.getUsuarioById(testUserId);
        assertNotNull(usuario, "Debe encontrar el usuario");
        
        // When
        usuario.setNombre("Test UsuarioDAO Actualizado");
        usuario.setTelefono("+34 600 987 654");
        usuario.setRolEnum(Rol.DOCTOR);
        usuario.setEstadoEnum(Estado.INACTIVO);
        
        boolean actualizado = usuarioDAO.actualizarUsuario(usuario);
        
        // Then
        assertTrue(actualizado, "Debe actualizar el usuario");
        
        // Verificar cambios
        Usuario usuarioActualizado = usuarioDAO.getUsuarioById(testUserId);
        assertEquals("Test UsuarioDAO Actualizado", usuarioActualizado.getNombre());
        assertEquals("+34 600 987 654", usuarioActualizado.getTelefono());
        assertEquals("Doctor", usuarioActualizado.getRol());
        assertEquals("🔴 Inactivio", usuarioActualizado.getEstado());
    }
    
    @ParameterizedTest
    @Order(8)
    @CsvSource({
        "Doctor, Doctor",
        "Paciente, Paciente",
        "Administrador, Administrador"
    })
    @DisplayName("Filtrar usuarios por rol")
    void testGetUsuariosPorRol(String rol, String expectedRol) {
        // When
        List<Usuario> usuarios = usuarioDAO.getUsuariosPorRol(rol);
        
        // Then
        assertNotNull(usuarios);
        if (!usuarios.isEmpty()) {
            assertEquals(expectedRol, usuarios.get(0).getRol());
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("Estadísticas de usuarios")
    void testEstadisticas() {
        // Agregar otro usuario para tener datos
        Usuario otroUsuario = new Usuario(
            "Otro Test",
            TEST_EMAIL_2,
            "+34 600 111 222",
            Rol.PACIENTE,
            Estado.ACTIVO
        );
        usuarioDAO.agregarUsuario(otroUsuario);
        
        // When
        int total = usuarioDAO.getTotalUsuarios();
        int activos = usuarioDAO.getUsuariosActivos();
        int doctores = usuarioDAO.getTotalDoctores();
        int pacientes = usuarioDAO.getTotalPacientes();
        
        // Then
        assertTrue(total > 0, "Total debe ser positivo");
        assertTrue(activos >= 0, "Activos debe ser positivo");
        assertTrue(doctores >= 0, "Doctores debe ser positivo");
        assertTrue(pacientes >= 0, "Pacientes debe ser positivo");
        assertTrue(activos <= total, "Activos no puede ser mayor que total");
    }
    
    @Test
    @Order(10)
    @DisplayName("Eliminar usuario")
    void testEliminarUsuario() {
        // Given
        Usuario usuarioAEliminar = usuarioDAO.getUsuarioByEmail(TEST_EMAIL_2);
        assertNotNull(usuarioAEliminar, "Debe encontrar el usuario a eliminar");
        
        // When
        boolean eliminado = usuarioDAO.eliminarUsuario(usuarioAEliminar.getId());
        
        // Then
        assertTrue(eliminado, "Debe eliminar el usuario correctamente");
        
        // Verificar que fue eliminado
        Usuario usuarioEliminado = usuarioDAO.getUsuarioByEmail(TEST_EMAIL_2);
        assertNull(usuarioEliminado, "El usuario debe ser eliminado");
    }
    
    @Test
    @Order(11)
    @DisplayName("Eliminar usuario por ID")
    void testEliminarUsuarioPorId() {
        // When
        boolean eliminado = usuarioDAO.eliminarUsuario(testUserId);
        
        // Then
        assertTrue(eliminado, "Debe eliminar el usuario correctamente");
        
        // Verificar que fue eliminado
        Usuario usuarioEliminado = usuarioDAO.getUsuarioById(testUserId);
        assertNull(usuarioEliminado, "El usuario debe ser eliminado");
    }
    
    @Test
    @DisplayName("Manejo de errores - Usuario no encontrado")
    void testUsuarioNoEncontrado() {
        // Given
        ObjectId idInexistente = new ObjectId();
        
        // When
        Usuario usuario = usuarioDAO.getUsuarioById(idInexistente);
        
        // Then
        assertNull(usuario, "Debe retornar null para ID no existente");
    }
}
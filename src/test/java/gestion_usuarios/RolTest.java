package gestion_usuarios;

import Model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el enum Rol")
class RolTest {
    
    @Test
    @DisplayName("Verificar valores del enum")
    void testValoresEnum() {
        Rol[] roles = Rol.values();
        
        assertEquals(4, roles.length);
        assertEquals(Rol.ADMINISTRADOR, roles[0]);
        assertEquals(Rol.DOCTOR, roles[1]);
        assertEquals(Rol.PACIENTE, roles[2]);
        assertEquals(Rol.PERSONAL, roles[3]);
    }
    
    @ParameterizedTest
    @EnumSource(Rol.class)
    @DisplayName("Test descripciones de roles")
    void testDescripciones(Rol rol) {
        switch (rol) {
            case ADMINISTRADOR:
                assertEquals("Administrador", rol.getDescripcion());
                break;
            case DOCTOR:
                assertEquals("Doctor", rol.getDescripcion());
                break;
            case PACIENTE:
                assertEquals("Paciente", rol.getDescripcion());
                break;
            case PERSONAL:
                assertEquals("Personal", rol.getDescripcion());
                break;
        }
    }
    
    @Test
    @DisplayName("Test valueOf")
    void testValueOf() {
        assertEquals(Rol.ADMINISTRADOR, Rol.valueOf("ADMINISTRADOR"));
        assertEquals(Rol.DOCTOR, Rol.valueOf("DOCTOR"));
        assertEquals(Rol.PACIENTE, Rol.valueOf("PACIENTE"));
        assertEquals(Rol.PERSONAL, Rol.valueOf("PERSONAL"));
    }
}

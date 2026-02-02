package gestion_usuarios;

import Model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para el enum Estado")
class EstadoTest {
    
    @Test
    @DisplayName("Verificar valores del enum")
    void testValoresEnum() {
        Estado[] estados = Estado.values();
        
        assertEquals(2, estados.length);
        assertEquals(Estado.ACTIVO, estados[0]);
        assertEquals(Estado.INACTIVO, estados[1]);
    }
    
    @ParameterizedTest
    @EnumSource(Estado.class)
    @DisplayName("Test descripciones de estados")
    void testDescripciones(Estado estado) {
        switch (estado) {
            case ACTIVO:
                assertEquals("🟢 Activio", estado.getDescripcion());
                break;
            case INACTIVO:
                assertEquals("🔴 Inactivio", estado.getDescripcion());
                break;
        }
    }
}
package Model;

public enum Rol {
    ADMINISTRADOR("Administrador"),
    DOCTOR("Doctor"),
    PACIENTE("Paciente"),
    PERSONAL("Personal");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    public static Rol fromString(String text) {
        for (Rol rol : Rol.values()) {
            if (rol.descripcion.equalsIgnoreCase(text)) {
                return rol;
            }
        }
        return PACIENTE;
    }
}
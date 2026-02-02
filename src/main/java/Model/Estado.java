package Model;

public enum Estado {
    ACTIVO("🟢 Activio"),
    INACTIVO("🔴 Inactivio");

    private final String descripcion;

    Estado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
    
    public static Estado fromString(String text) {
        for (Estado estado : Estado.values()) {
            if (estado.descripcion.equals(text)) {
                return estado;
            }
        }
        return ACTIVO;
    }
}
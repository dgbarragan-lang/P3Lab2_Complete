package Model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Usuario {
    private ObjectId id;
    
    @BsonProperty("nombre")
    private String nombre;
    
    @BsonProperty("email")
    private String email;
    
    @BsonProperty("telefono")
    private String telefono;
    
    @BsonProperty("rol")
    private String rol;
    
    @BsonProperty("estado")
    private String estado;
    
    @BsonProperty("ultimo_acceso")
    private String ultimoAcceso;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Usuario() {}

    public Usuario(String nombre, String email, String telefono, Rol rol, Estado estado) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol.getDescripcion();
        this.estado = estado.getDescripcion();
        this.ultimoAcceso = LocalDate.now().format(FORMATTER);
    }

    // Getters y Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(String ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    
    // Métodos de ayuda
    public Rol getRolEnum() {
        for (Rol r : Rol.values()) {
            if (r.getDescripcion().equals(rol)) {
                return r;
            }
        }
        return Rol.PACIENTE;
    }
    
    public Estado getEstadoEnum() {
        for (Estado e : Estado.values()) {
            if (e.getDescripcion().equals(estado)) {
                return e;
            }
        }
        return Estado.ACTIVO;
    }
    
    public void setRolEnum(Rol rol) {
        this.rol = rol.getDescripcion();
    }
    
    public void setEstadoEnum(Estado estado) {
        this.estado = estado.getDescripcion();
    }
    
    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
}